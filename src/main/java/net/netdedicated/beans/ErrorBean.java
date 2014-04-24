package net.netdedicated.beans;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: artemz
 * Date: 1/7/14
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorBean {
	public String errorMessage;

	public ErrorBean(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public ErrorBean(){}
	public String getErrorMessage() {
		return errorMessage;
	}
	public String toXML(){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(baos);
		encoder.writeObject(this);
		encoder.flush();
		encoder.close();
		return baos.toString();
	}
}
