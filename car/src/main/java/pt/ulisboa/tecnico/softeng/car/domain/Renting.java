package pt.ulisboa.tecnico.softeng.car.domain;

import org.joda.time.LocalDate;

import pt.ulisboa.tecnico.softeng.car.dataobjects.InvoiceData;
import pt.ulisboa.tecnico.softeng.car.exception.CarException;

import pt.ulisboa.tecnico.softeng.car.interfaces.TaxInterface;

public class Renting {
	private static String drivingLicenseFormat = "^[a-zA-Z]+\\d+$";
	private static int counter;

	private final String reference;
	private final String drivingLicense;
	private final LocalDate begin;
	private final LocalDate end;
	private int kilometers = -1;
	private final Vehicle vehicle;

	public Renting(String drivingLicense, LocalDate begin, LocalDate end, Vehicle vehicle, String buyerNIF) {
		checkArguments(drivingLicense, begin, end, vehicle);
		this.reference = Integer.toString(++Renting.counter);
		this.drivingLicense = drivingLicense;
		this.begin = begin;
		this.end = end;
		this.vehicle = vehicle;

		InvoiceData invoiceData = new InvoiceData(vehicle.getRentACar().getNIF(), buyerNIF, "CAR", vehicle.getPrice(), begin);
		TaxInterface.submitInvoice(invoiceData);
	}

	private void checkArguments(String drivingLicense, LocalDate begin, LocalDate end, Vehicle vehicle) {
		if (drivingLicense == null || !drivingLicense.matches(drivingLicenseFormat) || begin == null || end == null || vehicle == null
				|| end.isBefore(begin))
			throw new CarException();
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @return the drivingLicense
	 */
	public String getDrivingLicense() {
		return drivingLicense;
	}

	/**
	 * @return the begin
	 */
	public LocalDate getBegin() {
		return begin;
	}

	/**
	 * @return the end
	 */
	public LocalDate getEnd() {
		return end;
	}

	/**
	 * @return the vehicle
	 */
	public Vehicle getVehicle() {
		return vehicle;
	}

	/**
	 * @param begin
	 * @param end
	 * @return <code>true</code> if this Renting conflicts with the given date
	 *         range.
	 */
	public boolean conflict(LocalDate begin, LocalDate end) {
		if (end.isBefore(begin)) {
			throw new CarException("Error: end date is before begin date.");
		} else if ((begin.equals(this.getBegin()) || begin.isAfter(this.getBegin()))
				&& (begin.isBefore(this.getEnd()) || begin.equals(this.getEnd()))) {
			return true;
		} else if ((end.equals(this.getEnd()) || end.isBefore(this.getEnd()))
				&& (end.isAfter(this.getBegin()) || end.isEqual(this.getBegin()))) {
			return true;
		} else if ((begin.isBefore(this.getBegin()) && end.isAfter(this.getEnd()))) {
			return true;
		}

		return false;
	}

	/**
	 * Settle this renting and update the kilometers in the vehicle.
	 * 
	 * @param kilometers
	 */
	public void checkout(int kilometers) {
		this.kilometers = kilometers;
		this.vehicle.addKilometers(this.kilometers);
	}

}