package space.robinjoon.accountbook.dto;
// 각각의 자산 종류를 표현하는 객체.
public class Asset {
	private String assetName;
	private long value = 0;
	
	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	
	
}
