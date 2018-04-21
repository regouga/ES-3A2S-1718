package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.tax.dataobjects.InvoiceData;
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

public class IRS extends IRS_Base {
	public static IRS getIRS() {
		if (FenixFramework.getDomainRoot().getIrs() == null) {
			new IRS();
		}
		return FenixFramework.getDomainRoot().getIrs();
	}

	private IRS() {
		FenixFramework.getDomainRoot().setIrs(this);
	}
	
	public void delete(){
		FenixFramework.getDomainRoot().setIrs(null);
		
		for(ItemType itemType : getItemTypesSet()){
			itemType.delete();
		}
		
		for(TaxPayer taxPayer : getTaxPayersSet()){
			taxPayer.delete();
		}
				
		deleteDomainObject();
	}

	void addTaxPayer(TaxPayer taxPayer) {
		this.addTaxPayers(taxPayer);
	}

	public TaxPayer getTaxPayerByNIF(String NIF) {
		for (TaxPayer taxPayer : getTaxPayersSet()) {
			if (taxPayer.getNIF().equals(NIF)) {
				return taxPayer;
			}
		}
		return null;
	}

	void addItemType(ItemType itemType) {
		this.addItemTypes(itemType);
	}

	public ItemType getItemTypeByName(String name) {
		for (ItemType itemType : getItemTypesSet()) {
			if (itemType.getName().equals(name)) {
				return itemType;
			}
		}
		return null;
	}

	public static String submitInvoice(InvoiceData invoiceData) {
		IRS irs = IRS.getIRS();
		Seller seller = (Seller) irs.getTaxPayerByNIF(invoiceData.getSellerNIF());
		Buyer buyer = (Buyer) irs.getTaxPayerByNIF(invoiceData.getBuyerNIF());
		ItemType itemType = irs.getItemTypeByName(invoiceData.getItemType());
		Invoice invoice = new Invoice(invoiceData.getValue(), invoiceData.getDate(), itemType, seller, buyer);

		return invoice.getReference();
	}

	public void removeItemTypes() {
		for(ItemType itemType : getItemTypesSet()){
			removeItemTypes(itemType);
		}
	}

	public void removeTaxPayers() {
		for(TaxPayer taxPayer: getTaxPayersSet()){
			removeTaxPayers(taxPayer);
		}
	}

	public void clearAll() {
		removeItemTypes();
		removeTaxPayers();
	}

	public static void cancelInvoice(String reference) {
		if (reference == null || reference.isEmpty()) {
			throw new TaxException();
		}

		Invoice invoice = IRS.getIRS().getInvoiceByReference(reference);

		if (invoice == null) {
			throw new TaxException();
		}

		invoice.cancel();
	}

	private Invoice getInvoiceByReference(String reference) {
		for (TaxPayer taxPayer : getTaxPayersSet()) {
			Invoice invoice = taxPayer.getInvoiceByReference(reference);
			if (invoice != null) {
				return invoice;
			}
		}
		return null;
	}

}
