package space.robinjoon.accountbook.dto;

import java.util.ArrayList;
import java.util.List;

//각 월의 가계부를 표현하는 객체.
public class AccountBook {
	private final String yearMonth;
	private final long totalIncome;
	private final long totlaExpenditure;
	private final List<Category> incomeCategoryList;
	private final List<Category> expenditureCategoryList;
	private final List<Asset> assetList;
	private final List<Account> incomeAccountList;
	private final List<Account> expenditureAccountList;
	private final List<ConversionAccount> conversionAccountList;
	
	public static class Builder{
		private String yearMonth;
		private long totalIncome = 0;
		private long totlaExpenditure = 0;
		private List<Category> incomeCategoryList = new ArrayList<>();
		private List<Category> expenditureCategoryList = new ArrayList<>();
		private List<Asset> assetList = new ArrayList<>();
		private List<Account> incomeAccountList = new ArrayList<>();
		private List<Account> expenditureAccountList = new ArrayList<>();
		private List<ConversionAccount> conversionAccountList = new ArrayList<>();
		public Builder setYearMonth(String yearMonth) {
			this.yearMonth = yearMonth;
			return this;
		}
		public Builder setTotalIncome(long totalIncome) {
			this.totalIncome = totalIncome;
			return this;
		}
		public Builder setTotlaExpenditure(long totlaExpenditure) {
			this.totlaExpenditure = totlaExpenditure;
			return this;
		}
		public Builder setIncomeCategoryList(List<Category> incomeCategoryList) {
			this.incomeCategoryList = incomeCategoryList;
			return this;
		}
		public Builder setExpenditureCategoryList(List<Category> expenditureCategoryList) {
			this.expenditureCategoryList = expenditureCategoryList;
			return this;
		}
		public Builder setAssetList(List<Asset> assetList) {
			this.assetList = assetList;
			return this;
		}
		public Builder setIncomeAccountList(List<Account> incomeAccountList) {
			this.incomeAccountList = incomeAccountList;
			return this;
		}
		public Builder setExpenditureAccountList(List<Account> expenditureAccountList) {
			this.expenditureAccountList = expenditureAccountList;
			return this;
		}
		public Builder setConversionAccountList(List<ConversionAccount> conversionAccountList) {
			this.conversionAccountList = conversionAccountList;
			return this;
		}
		public AccountBook build() {
			return new AccountBook(this);
		}
		
	}
	private AccountBook(Builder builder) {
		this.assetList =builder.assetList;
		this.conversionAccountList = builder.conversionAccountList;
		this.expenditureAccountList = builder.expenditureAccountList;
		this.expenditureCategoryList =builder.expenditureCategoryList;
		this.incomeAccountList = builder.incomeAccountList;
		this.incomeCategoryList = builder.incomeCategoryList;
		this.totalIncome = builder.totalIncome;
		this.totlaExpenditure = builder.totlaExpenditure;
		this.yearMonth = builder.yearMonth;
	}
	public AccountBook() {
		this.assetList = null;
		this.conversionAccountList = null;
		this.expenditureAccountList = null;
		this.expenditureCategoryList = null;
		this.incomeAccountList = null;
		this.incomeCategoryList = null;
		this.totalIncome = 0;
		this.totlaExpenditure = 0;
		this.yearMonth = "";
	}
	public String getYearMonth() {
		return yearMonth;
	}
	public long getTotalIncome() {
		return totalIncome;
	}
	public long getTotlaExpenditure() {
		return totlaExpenditure;
	}
	public List<Category> getIncomeCategoryList() {
		return incomeCategoryList;
	}
	public List<Category> getExpenditureCategoryList() {
		return expenditureCategoryList;
	}
	public List<Asset> getAssetList() {
		return assetList;
	}
	public List<Account> getIncomeAccountList() {
		return incomeAccountList;
	}
	public List<Account> getExpenditureAccountList() {
		return expenditureAccountList;
	}
	public List<Account> getAllAccountList(){
		List<Account> accountList = new ArrayList<Account>();
		accountList.addAll(incomeAccountList);
		accountList.addAll(expenditureAccountList);
		return accountList;
	}

	public List<ConversionAccount> getConversionAccountList() {
		return conversionAccountList;
	}


	
	
}
