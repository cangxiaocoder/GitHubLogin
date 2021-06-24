package com.cx.pojo;

import java.io.Serializable;
 
public class Respones implements Serializable {

	private static final long serialVersionUID = 1L;

	private int responeCde;

	private String result;

	public int getResponeCde() {
		return responeCde;
	}

	public void setResponeCde(int responeCde) {
		this.responeCde = responeCde;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}