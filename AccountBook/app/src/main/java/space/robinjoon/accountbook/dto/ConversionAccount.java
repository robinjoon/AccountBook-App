package space.robinjoon.accountbook.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.StringTokenizer;

// 각 자산(Asset)간의 변환기록을 저장하는 객체
public class ConversionAccount{
	private final long aid;
	private final long value;
	@JsonFormat(shape=Shape.STRING)
	private final String time;
	private final String from;
	private final String to;
	private final String memo;
	private final String yearMonth;
	
	public static class Builder{
		private long aid;
		private long value;
		private String time;
		private String from;
		private String to;
		private String memo;
		private String yearMonth;
		
		public Builder setAid(long aid) {
			this.aid = aid;
			return this;
		}
		public Builder setValue(long value) {
			if(value<=0)
				throw new IllegalArgumentException("value must be positive");
			this.value = value;
			return this;
		}
		public Builder setTime(String time) {
			this.time = time;
			return this;
		}
		public Builder setFrom(String from) {
			this.from = from;
			return this;
		}
		public Builder setTo(String to) {
			this.to = to;
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
		public ConversionAccount build() {
			return new ConversionAccount(this);
		}
		
	}
	private ConversionAccount(Builder builder) {
		this.aid = builder.aid;
		this.from = builder.from;
		this.memo = builder.memo;
		this.time = builder.time;
		this.to = builder.to;
		this.value = builder.value;
		this.yearMonth = builder.yearMonth;
	}
	public ConversionAccount() {
		this.aid = 0;
		this.from = "";
		this.memo = "";
		this.time = null;
		this.to = "";
		this.value = 0;
		this.yearMonth = "";
	}
	public long getAid() {
		return aid;
	}
	public long getValue() {
		return value;
	}
	public String getTime() {
		return time;
	}
	public String getFrom() {
		return from;
	}
	public String getTo() {
		return to;
	}
	public String getMemo() {
		return memo;
	}
	public String getYearMonth() {
		return yearMonth;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(time,"T");
		st.nextToken();
		sb.append(st.nextToken().substring(0,5)).append(" ").append(from).append(">>").append(to).append(" ").append(new DecimalFormat("###,###₩").format(value)).append("\n").append(memo);
		return sb.toString();
	}
}
