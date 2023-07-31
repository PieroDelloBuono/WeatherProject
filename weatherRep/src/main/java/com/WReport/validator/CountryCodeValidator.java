package com.WReport.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CountryCodeValidator {
	
	@Value("${accepted.country.codes}")
	private String acceptedCountry;
	
	public boolean isCountryCodeValid(String countryCode) {
		String[] countryCodes = acceptedCountry.split(",");
			for(String code : countryCodes) {
				if(code.trim().equalsIgnoreCase(countryCode)) {
					return true;
				}
			}
		return false;
	}

}
