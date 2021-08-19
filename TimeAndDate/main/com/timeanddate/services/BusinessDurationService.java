package com.timeanddate.services;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.w3c.dom.DOMException;

import com.timeanddate.services.common.AuthenticationException;
import com.timeanddate.services.common.ServerSideException;
import com.timeanddate.services.common.StringUtils;
import com.timeanddate.services.common.UriUtils;
import com.timeanddate.services.common.WebClient;
import com.timeanddate.services.common.XmlUtils;
import com.timeanddate.services.common.IPredicate;

import com.timeanddate.services.dataTypes.places.LocationId;
import com.timeanddate.services.dataTypes.time.TADDateTime;
import com.timeanddate.services.dataTypes.businessdays.BusinessDaysFilterType;

/**
 *
 * @author Daniel Alvsåker {@literal <daniel@timeanddate.com>}
 *
 */
public class BusinessDurationService extends BaseService {

	private boolean _includeDays;

	private boolean _includeLastDate;

	private EnumSet<BusinessDaysFilterType> _filter;

	/**
	 * The businessduration service can be used to calculate the 
	 * number of business days between a specified start date and end date.
	 *
	 * @param accessKey
	 *            Access key.
	 * @param secretKey
	 *            Secret key.
	 * @throws AuthenticationException
	 * 			  Encryption of the authentication failed
	 */
	public BusinessDurationService(String accessKey, String secretKey)
			throws AuthenticationException {
		super(accessKey, secretKey, "businessduration");
		_includeDays = false;
		_includeLastDate = false;
		_filter = EnumSet.of(BusinessDaysFilterType.WEEKENDHOLIDAYS);
	}

	public void setIncludeDays(boolean bool) {
		_includeDays = bool;
	}

	public boolean getIncludeDays() {
		return _includeDays;
	}

	public void setIncludeLastDate(boolean bool) {
		_includeLastDate = bool;
	}

	public boolean getIncludeLastDate() {
		return _includeLastDate;
	}

	public void setFilter(EnumSet<BusinessDaysFilterType> filter) {
		_filter = filter;
	}

	public void addFilter(BusinessDaysFilterType filter) {
		_filter.add(filter);
	}

	public EnumSet<BusinessDaysFilterType> getFilter() {
		return _filter;
	}

	/**
	 * The businessduration service can be used to calculate the 
	 * number of business days between a specified start date and end date.
	 *
	 * @param startDate
	 * 		Start date to calculate from.
	 * @param endDate
	 * 		End date to calculate to.
	 * @param placeId
	 * 		The places identifier.
	 * @return The calculated result and geographical information.
	 * @throws ServerSideException
	 * 		The server produced an error message.
	 * @throws IllegalArgumentException
	 * 		A required argument was not as expected.
	 */
	public BusinessDuration getDuration(TADDateTime startDate, TADDateTime endDate, LocationId placeId)
		throws IllegalArgumentException, ServerSideException {
		return executeBusinessDuration(startDate, endDate, placeId);
	}

	private BusinessDuration executeBusinessDuration(TADDateTime startDate, TADDateTime endDate, LocationId placeId)
			throws IllegalArgumentException, ServerSideException {

		Map<String, String> arguments = getArguments(startDate, endDate, placeId);
		String result = new String();

		try {
			String query = UriUtils.BuildUriString(arguments);
			URL uri = new URL(Constants.EntryPoint + ServiceName + query);
			WebClient client = new WebClient();
			result = client.downloadString(uri);
			XmlUtils.checkForErrors(result);

		} catch (DOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return BusinessDuration.fromXml(result);
	}

	private Map<String, String> getArguments(TADDateTime startDate, TADDateTime endDate, LocationId placeId) {
		Map<String, String> args = new HashMap<String, String>(
				AuthenticationOptions);

		String filter = getFilterTypesAsStr();
		args.put("startdt", startDate.toString());
		args.put("enddt", endDate.toString());
		args.put("placeid", placeId.getId());
		args.put("include", StringUtils.BoolToNum(_includeDays));
		args.put("includelastdate", StringUtils.BoolToNum(_includeLastDate));
		args.put("lang", Language);
		args.put("version", Integer.toString(Version));
		args.put("out", Constants.DefaultReturnFormat);
		args.put("verbosetime", Integer.toString(Constants.DefaultVerboseTimeValue));

		if (filter != null && !filter.isEmpty())
			args.put("filter", filter);

		return args;
	}

	private String getFilterTypesAsStr() {
		if (_filter == null)
			return "";

		ArrayList<String> includedStrings = new ArrayList<String>();
		for (final BusinessDaysFilterType type : BusinessDaysFilterType.values()) {
			if (_filter.contains(type))
				includedStrings
						.add(StringUtils.resolveBusinessDaysFilter(where.of(type)).Command);
		}

		String included = StringUtils.join(includedStrings, ",");
		return included;
	}

	private IPredicate<BusinessDaysFilterType> where = new IPredicate<BusinessDaysFilterType>() {
		BusinessDaysFilterType type;

		public boolean is(BusinessDaysFilterType t) {
			return t == type;
		}

		public IPredicate<BusinessDaysFilterType> of(BusinessDaysFilterType t) {
			type = t;
			return this;
		}
	};
}
