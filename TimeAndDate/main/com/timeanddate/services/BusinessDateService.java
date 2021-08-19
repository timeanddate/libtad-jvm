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
public class BusinessDateService extends BaseService {

	private boolean _includeDays;

	private int _repeat;

	private EnumSet<BusinessDaysFilterType> _filter;

	/**
	 * The businessdate service can be used to find a business
	 * date from a specified number of days.
	 *
	 * @param accessKey
	 *            Access key.
	 * @param secretKey
	 *            Secret key.
	 * @throws AuthenticationException
	 * 			  Encryption of the authentication failed
	 */
	public BusinessDateService(String accessKey, String secretKey)
			throws AuthenticationException {
		super(accessKey, secretKey, "businessdate");
		_includeDays = false;
		_filter = EnumSet.of(BusinessDaysFilterType.WEEKENDHOLIDAYS);
	}

	public void setIncludeDays(boolean bool) {
		_includeDays = bool;
	}

	public boolean getIncludeDays() {
		return _includeDays;
	}

	public void setRepeat(int repeat) {
		_repeat = repeat;
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
	 * The businessdate service can be used to find a business
	 * date from a specified number of days.
	 *
	 * @param startDate
	 * 		Start date to calculate from.
	 * @param days
	 * 		Days to add. Can be a list of several days.
	 * @param placeId
	 * 		The places identifier.
	 * @return The calculated result and geographical information.
	 * @throws ServerSideException
	 * 		The server produced an error message.
	 * @throws IllegalArgumentException
	 * 		A required argument was not as expected.
	 */
	public BusinessDates addDays(TADDateTime startDate, List<Integer> days, LocationId placeId)
		throws IllegalArgumentException, ServerSideException {
		return executeBusinessDate("add", startDate, days, placeId);
	}

	/**
	 * The businessdate service can be used to find a business
	 * date from a specified number of days.
	 *
	 * @param startDate
	 * 		Start date to calculate from.
	 * @param days
	 * 		Days to add.
	 * @param placeId
	 * 		The places identifier.
	 * @return The calculated result and geographical information.
	 * @throws ServerSideException
	 * 		The server produced an error message.
	 * @throws IllegalArgumentException
	 * 		A required argument was not as expected.
	 */
	public BusinessDates addDays(TADDateTime startDate, int days, LocationId placeId)
		throws IllegalArgumentException, ServerSideException {
		List<Integer> list = new ArrayList<Integer>();
		list.add(days);

		return executeBusinessDate("add", startDate, list, placeId);
	}

	/**
	 * The businessdate service can be used to find a business
	 * date from a specified number of days.
	 *
	 * @param startDate
	 * 		Start date to calculate from.
	 * @param days
	 * 		Days to subtract. Can be a list of several days.
	 * @param placeId
	 * 		The places identifier.
	 * @return The calculated result and geographical information.
	 * @throws ServerSideException
	 * 		The server produced an error message.
	 * @throws IllegalArgumentException
	 * 		A required argument was not as expected.
	 */
	public BusinessDates subtractDays(TADDateTime startDate, List<Integer> days, LocationId placeId)
		throws IllegalArgumentException, ServerSideException {
		return executeBusinessDate("subtract", startDate, days, placeId);
	}

	/**
	 * The businessdate service can be used to find a business
	 * date from a specified number of days.
	 *
	 * @param startDate
	 * 		Start date to calculate from.
	 * @param days
	 * 		Days to subtract.
	 * @param placeId
	 * 		The places identifier.
	 * @return The calculated result and geographical information.
	 * @throws ServerSideException
	 * 		The server produced an error message.
	 * @throws IllegalArgumentException
	 * 		A required argument was not as expected.
	 */
	public BusinessDates subtractDays(TADDateTime startDate, int days, LocationId placeId)
		throws IllegalArgumentException, ServerSideException {
		List<Integer> list = new ArrayList<Integer>();
		list.add(days);

		return executeBusinessDate("subtract", startDate, list, placeId);
	}

	private BusinessDates executeBusinessDate(String op, TADDateTime startDate, List<Integer> days, LocationId placeId)
			throws IllegalArgumentException, ServerSideException {

		Map<String, String> arguments = getArguments(op, startDate, days, placeId);
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

		return BusinessDates.fromXml(result);
	}

	private Map<String, String> getArguments(String op, TADDateTime startDate, List<Integer> days, LocationId placeId) {
		Map<String, String> args = new HashMap<String, String>(
				AuthenticationOptions);

		String filter = getFilterTypesAsStr();
		args.put("op", op);
		args.put("startdt", startDate.toString());
		args.put("placeid", placeId.getId());
		args.put("include", StringUtils.BoolToNum(_includeDays));
		args.put("days", StringUtils.join(days, ","));
		args.put("lang", Language);
		args.put("version", Integer.toString(Version));
		args.put("out", Constants.DefaultReturnFormat);
		args.put("verbosetime", Integer.toString(Constants.DefaultVerboseTimeValue));

		if (filter != null && !filter.isEmpty())
			args.put("filter", filter);

		if (_repeat > 0)
			args.put("repeat", Integer.toString(_repeat));

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
