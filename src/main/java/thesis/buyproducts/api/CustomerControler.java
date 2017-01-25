package thesis.buyproducts.api;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import thesis.buyproducts.entity.Customer;
import thesis.buyproducts.execption.StrategyException;
import thesis.buyproducts.execption.RestApiException;
import thesis.buyproducts.execption.ServiceException;
import thesis.buyproducts.execption.domaintype.StrategyExceptionType;
import thesis.buyproducts.execption.domaintype.ServiceExceptionType;
import thesis.buyproducts.mapper.CustomerMapperBean;
import thesis.buyproducts.service.CustomerService;
import thesis.buyproducts.strategy.BuyWithPointsStrategy;
import thesis.buyproducts.strategy.PurchaseProcessorStrategy;
import thesis.buyproducts.vo.BuyWithPointsVO;
import thesis.buyproducts.vo.CustomerStateAccountVO;
import thesis.buyproducts.vo.CustomerVO;

@RestController
@RequestMapping(value = "/customer")
public class CustomerControler {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerMapperBean customerMapper;

	@Autowired
	private PurchaseProcessorStrategy customerStateAccountStateStrategy;

	@Autowired
	private BuyWithPointsStrategy buyWithPointsStrategy;

	@ResponseStatus(code = HttpStatus.CREATED)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public CustomerVO persist(@RequestBody @Valid CustomerVO customerVO) throws RestApiException {
		try {
			Customer customer = customerMapper.mapEntityFrom(customerVO);
			customerService.persist(customer);
			return customerMapper.mapVOFrom(customer);
		} catch (ServiceException e) {
			if (e.getServiceExceptionType() == ServiceExceptionType.USERNAME_NOT_UNIQUE) {
				throw RestApiException.userNameNotUnique(e.getMessage());
			} else if (e.getServiceExceptionType() == ServiceExceptionType.ERROR_PERSISTING) {
				throw RestApiException.errorPersisting(e.getMessage());
			}
		}
		throw RestApiException.unhandledException("Creating a new customer went wrong!");
	}

	@ResponseStatus(code = HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public CustomerVO updateCustomer(@RequestBody @Valid CustomerVO customerVO) throws RestApiException {
		try {
			return customerMapper.mapVOFrom(customerService.update(customerMapper.mapEntityFrom(customerVO)));
		} catch (ServiceException e) {
			if (e.getServiceExceptionType() == ServiceExceptionType.USERNAME_NOT_UNIQUE) {
				throw RestApiException.userNameNotUnique(e.getMessage());
			} else if (e.getServiceExceptionType() == ServiceExceptionType.NOT_FOUND) {
				throw RestApiException.notFound(e.getMessage());
			} else {
				throw RestApiException.errorUpdating(e.getMessage());
			}
		}
	}

	@ResponseStatus(code = HttpStatus.FOUND)
	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public CustomerVO findByPK(@PathVariable("id") Long id) throws RestApiException {
		try {
			return customerMapper.mapVOFrom(customerService.findById(id));
		} catch (ServiceException e) {
			throw RestApiException.notFound(e.getMessage());
		}
	}

	@ResponseStatus(code = HttpStatus.FOUND)
	@RequestMapping(value = "/username/{username}/", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomerVO findByUserName(@PathVariable("username") String userName) throws RestApiException {
		try {
			return customerMapper.mapVOFrom(customerService.findByUserName(userName));
		} catch (ServiceException e) {
			throw RestApiException.notFound(e.getMessage());
		}
	}

	@RequestMapping(value = "/username/{username}/amount/{amount}/", method = RequestMethod.PUT)
	public CustomerStateAccountVO processPurchase(@PathVariable("username") String username,
			@PathVariable("amount") @Min(value = 0) Double amount) throws RestApiException {
		try {
			return customerStateAccountStateStrategy.processPurchase(username, amount);
		} catch (StrategyException exception) {
			if (exception.getExceptionType() == StrategyExceptionType.AMOUNT_NOT_VALID) {
				throw RestApiException.invalidAmount(exception.getMessage());
			} else if (exception.getExceptionType() == StrategyExceptionType.COULT_NOT_FIND_USER) {
				throw RestApiException.notFound(exception.getMessage());
			} else if (exception.getExceptionType() == StrategyExceptionType.ERROR_UPDAING_USER) {
				throw RestApiException.errorUpdating(exception.getMessage());
			}
		}
		return null;
	}

	@RequestMapping(value = "/usepoints/username/{username}/amount/{amount}/", method = RequestMethod.PUT)
	public BuyWithPointsVO buyWithPoints(@PathVariable("username") String username,
			@PathVariable("amount") @Min(value = 0) Double amount) throws RestApiException {
		try {
			return buyWithPointsStrategy.processPurchaseWithPoints(username, amount);
		} catch (StrategyException exception) {
			if (exception.getExceptionType() == StrategyExceptionType.COULT_NOT_FIND_USER) {
				throw RestApiException.notFound(exception.getMessage());
			} else if (exception.getExceptionType() == StrategyExceptionType.ERROR_UPDAING_USER) {
				throw RestApiException.errorUpdating(exception.getMessage());
			} else if (exception.getExceptionType() == StrategyExceptionType.AMOUNT_NOT_VALID) {
				throw RestApiException.invalidAmount(exception.getMessage());
			} else if (exception.getExceptionType() == StrategyExceptionType.ERROR_PROCESSING_POINTS) {
				throw RestApiException.errorProcessingPoints(exception.getMessage());
			}
		}
		return null;
	}

}
