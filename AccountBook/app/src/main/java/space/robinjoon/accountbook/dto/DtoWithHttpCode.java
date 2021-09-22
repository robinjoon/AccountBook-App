package space.robinjoon.accountbook.dto;

public class DtoWithHttpCode<T> {
	private int httpCode;
	private T data;
	private String possibleRequests[];
	public DtoWithHttpCode(){};
	public DtoWithHttpCode(int httpCode, T data, String[] possibleRequests){
		this.httpCode = httpCode;
		this.data = data;
		this.possibleRequests = possibleRequests;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public T getData() {
		return data;
	}

	public String[] getPossibleRequests() {
		return possibleRequests;
	}
	
}
