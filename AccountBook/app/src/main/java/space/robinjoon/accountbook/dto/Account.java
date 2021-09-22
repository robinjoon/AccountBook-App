package space.robinjoon.accountbook.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import java.text.DecimalFormat;
import java.util.StringTokenizer;

// 가계부의 한 칸에 적힐 각각의 수익 혹은 지출기록을 저장하는 객체
public class Account {
	private final long aid;
	private final String accountType;
	private final long value;
	@JsonFormat(shape=Shape.STRING)
	private final String time;
	private final String assetName;
	private final String category;
	private final String memo;
	private final String yearMonth;
	
	public static class Builder {
		private long aid;
		private String accountType;
		private long value;
		private String time;
		private String assetName;
		private String category;
		private String memo;
		private String yearMonth;
		public Builder() {	
		}
		public Account build() {
			return new Account(this);
		}
		public Builder setAid(long aid) {
			this.aid = aid;
			return this;
		}
		public Builder setAccountType(String accountType) {
			this.accountType = accountType;
			return this;
		}
		/**
		 * @throws IllegalArgumentException if value is not positive.
		 * */
		public Builder setValue(long value) {
			if(value > 0) {
				this.value = value;
			}else {
				throw new IllegalArgumentException("value must be positive");
			}
			return this;
		}
		public Builder setTime(String time) {
			this.time = time;
			return this;
		}
		public Builder setAssetName(String assetName) {
			this.assetName = assetName;
			return this;
		}
		public Builder setCategory(String category) {
			this.category = category;
			return this;
		}
		public Builder setMemo(String memo) {
			this.memo = memo;
			return this;
		}
		public Builder setYearMonth(String yearMonth) {
			this.yearMonth = yearMonth;
			return this;
		}
		
	}
	private Account(Builder builder) {
		this.accountType = builder.accountType;
		this.aid = builder.aid;
		this.assetName = builder.assetName;
		this.category =builder.category;
		this.memo = builder.memo;
		this.time = builder.time;
		this.value = builder.value;
		this.yearMonth = builder.yearMonth;
	}
	public Account() {
		this.accountType = "";
		this.aid = 0;
		this.assetName = "";
		this.category = "";
		this.memo = "";
		this.time = null;
		this.value = 0;
		this.yearMonth = "";
	}
	public long getAid() {
		return aid;
	}

	public String getAccountType() {
		return accountType;
	}

	public long getValue() {
		return value;
	}

	public String getTime() {
		return time;
	}

	public String getAssetName() {
		return assetName;
	}

	public String getCategory() {
		return category;
	}

	public String getMemo() {
		return memo;
	}

	public String getYearMonth() {
		return yearMonth;
	}

	@Override
	public String toString() {
		String sign="-";
		if(accountType.equals("Income"))sign="+";
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(time,"T");
		st.nextToken();
		sb.append(st.nextToken().substring(0,5)).append(" ").append(assetName).append(" ").append(sign).append(" ").append(new DecimalFormat("###,###₩").format(value)).append("\n").append(memo);
		return sb.toString();
	}
	
}
