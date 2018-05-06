package pt.ulisboa.tecnico.softeng.broker.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;
import pt.ulisboa.tecnico.softeng.broker.services.local.BrokerInterface;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.ClientData;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BrokerData.CopyDepth;

@Controller
@RequestMapping(value = "/brokers/{brokerCode}/clients")
public class ClientController {
	
	private static Logger logger = LoggerFactory.getLogger(ClientController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String ClientForm(Model model,  @PathVariable String brokerCode) {
		logger.info("clientForm");
		model.addAttribute("client", new ClientData());
		model.addAttribute("broker", BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.CLIENTS));
		return "clients";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String ClientSubmit(Model model, @ModelAttribute ClientData clientData,  @PathVariable String brokerCode) {
		
		logger.info("clientSubmit brokerCode:{}, iban:{}, nif:{}, drivingLicense:{}, age:{}", brokerCode, clientData.getIban(), clientData.getNif(), clientData.getDrivingLicense(), clientData.getAge());

		try {
			
			BrokerInterface.createClient(brokerCode, clientData);
			
		} catch (BrokerException be) {
			model.addAttribute("error", "Error: it was not possible to create the client");
			model.addAttribute("client", clientData);
			model.addAttribute("broker", BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.CLIENTS));
			return "clients";
		}

		return "redirect:/brokers/" + brokerCode + "/clients";
		
	}
}
