Time And Date JVM API
======================================

Time and Date APIs support looking up several different locations and IDs. As of API version 2 the following variations are available:

* Numeric ID (e.g. 187)
* Textual ID (e.g. "usa/las-vegas")
* Coordinates (e.g. "+59.743+10.204")
* Airports (e.g. "a-ENZV")

The class [LocationId](http://services.timeanddate.com/api/doc/v2/type-locationid.html) is used to set the appropriate location ID. Airports categorize the same way as textual ID.

An access key and a secret key is required to use the API. If you are not already a Time and Date API user, please see our [API offers](https://services.timeanddate.com/api/packages/) to get a free 3 month trial. For more information, see our [API Services page](https://services.timeanddate.com/).
 
Astronomy Service
--------------------------------------
  
Get astronomy information for a place on a date by textual ID:
         
         LocationId place = new LocationId("usa/anchorage");
         TADDateTime date = new TADDateTime(2015, 1, 1);
         AstronomyService service = new AstronomyService('accessKey', 'secretKey');
         List<AstronomyLocation> astroInfo = service.getAstronomicalInfo(AstronomyObjectType.Sun, place, date);
         
Get astronomy information for a place between two dates by numeric ID:
 
         LocationId place = new LocationId(187);
         TADDateTime startDate = new TADDateTime(2015, 1, 1);
         TADDateTime endDate = new TADDateTime(2015, 1, 30);
         AstronomyService service = new AstronomyService('accessKey', 'secretKey');
         List<AstronomyLocation> astroInfo = service.getAstronomicalInfo(AstronomyObjectType.Moon, place, startDate, endDate);

Retrieve specific astronomy events by coordinates:

        Coordinates coordinates = new Coordinates(59.743m, 10.204m);
        LocationId place = new LocationId(coordinates);
        TADDateTime date = new TADDateTime(2015, 1, 1);
        AstronomyService service = new AstronomyService('accessKey', 'secretKey');

        service.Types = EnumSet.of(AstronomyEventClass.Meridian, AstronomyEventClass.NauticalTwilight);

        List<AstronomyLocation> astroInfo = service.getAstronomicalInfo(AstronomyObjectType.Moon, place, startDate, endDate);

Other options:

        // Adds the DateTime-object ISOTime to every astronomical day
        service.setIncludeISOTime(true);

        // Adds the DateTime-object UTCTime to every astronomical day
        service.setIncludeUTCTime(true);

        // Adds a search radius if GetAstronomicalInfo is used with coordinates
        service.setRadius(50); // km


Convert Time Service
--------------------------------------

Convert time from a location:

        LocationId place = new LocationId("norway/oslo");
        Calendar date = Calendar.getInstance();
        ConvertTimeService service = new ConvertTimeService('accessKey', 'secretKey');
        ConvertedTimes convertedTime = service.convertTime(place, date);

Convert time from a location using an [ISO 8601](http://services.timeanddate.com/api/doc/v2/type-isotime.html)-string:

        ...
        ConvertedTimes convertedTime = service.convertTime(place, "2015-04-21T16:45:00");

Convert time from one location to multiple locations:

        List<LocationId> listOfLocations = new ArrayList<LocationId>();
        listOfLocations.add(new LocationId("usa/las-vegas"));
        listOfLocations.add(new LocationId(179);
        
        LocationId place = new LocationId("oslo/norway");
        ConvertTimeService service = new ConvertTimeService('accessKey', 'secretKey');
        ConvertedTimes result = service.convertTime(place, Calendar.getInstance(), listOfLocations);

Other options:

        // Add TimeChanges for each location
        service.setIncludeTimeChanges(true);

        // Add Timezone information for each location
        service.setIncludeTimezoneInformation(true);

        // Search for a place by a specified radius
        service.setRadius(50); // km


Daylight Saving Time Service
--------------------------------------

Get all daylight saving times:

        DaylightSavingTimeService service = new DSTService('accessKey', 'secretKey');
        List<DST> allDST = service.getDaylightSavingTime();

Get daylight saving time for a specified year:

        DaylightSavingTimeService service = new DSTService('accessKey', 'secretKey');
        List<DST> result = service.getDaylightSavingTime(2014);

Get daylight saving time for a specified [ISO3166-1 (Alpha2)](http://services.timeanddate.com/api/doc/v2/type-isocountry.html) country code:

        DaylightSavingTimeService service = new DSTService('accessKey', 'secretKey');
        List<DST> result = service.getDaylightSavingTime("no");

Get daylight saving time for a specified [ISO3166-1 (Alpha2)](http://services.timeanddate.com/api/doc/v2/type-isocountry.html) country code and year:

        DaylightSavingTimeService service = new DSTService('accessKey', 'secretKey');
        List<DST> result = service.getDaylightSavingTime("no", 2014);

   
Other options:
       
       // Add TimeChanges to each location
       service.setIncludeTimeChanges(true);

       // Return only countries which have DST
       service.setIncludeOnlyDstCountries(true);

       // Add locations for every country
       service.setIncludePlacesForEveryCountry(true);

        
Dial Code Service
--------------------------------------

Get dial code for a location:

        LocationId osloId = new LocationId("norway/oslo");
        DialCodeService service = new DialCodeService('accessKey', 'secretKey');
        DialCodes result = service.getDialCode(osloId);

Get dial code to a location, from a location:

        LocationId osloId = new LocationId("norway/oslo")
        LocationId newYorkId = new LocationId("usa/new-york");
        DialCodeService service = new DialCodeService('accessKey', 'secretKey');
        DialCodes result = service.getDialCode(osloId, newYorkId);

Get dial code results with a local number:

        LocationId osloId = new LocationId("norway/oslo")
        LocationId newYorkId = new LocationId("usa/new-york");
        int number = 51515151;
        DialCodeService service = new DialCodeService('accessKey', 'secretKey');
        DialCodes result = service.getDialCode(osloId, newYorkId, number);

Other options:

        // Do not include locations in return value
        service.setIncludeLocations(false);

        // Do not include current time in return value
        service.setIncludeCurrentTime(false);

        // Do not include coordinates to locations in return value
        service.setIncludeCoordinates(false);

        // Do not include Timezone Information in return value
        service.setIncludeTimezoneInformation(false);

Holidays Service
--------------------------------------

Get all holidays for a country by [ISO3166-1 (Alpha2)](http://services.timeanddate.com/api/doc/v2/type-isocountry.html) country code:

        String country = "no";
        HolidaysService service = new HolidaysService('accessKey', 'secretKey');
        List<Holiday> result = service.getHolidaysForCountry(country);

Get all holidays for a country by year and [ISO3166-1 (Alpha2)](http://services.timeanddate.com/api/doc/v2/type-isocountry.html) country code:

        String country = "no";
        int year = 2014;
        HolidaysService service = new HolidaysService('accessKey', 'secretKey');
        List<Holiday> result = service.getHolidaysForCountry(country, 2014);

Get specific holidays for a country:

        String country = "no";
        HolidaysService service = new HolidaysService('accessKey', 'secretKey');
        service.Types = EnumSet.of(HolidayType.Federal, HolidayType.Weekdays);
        List<Holiday> result = service.getHolidaysForCountry(country);

Places Service
--------------------------------------

Get all places in Time and Date (these can be used to look up data in other services):

        PlacesService service = new  PlacesService('accessKey', 'secretKey')
        List<Place> result = service.getPlaces();

Other options:

        // Do not include coordinates in return value
        service.setIncludeCoordinates(false);

Time Service
--------------------------------------

Get current time for a place:

        LocationId place = new LocationId(179);
        TimeService service = new TimeService('accessKey', 'secretKey');
        List<Location> result = service.getCurrentTimeForPlace(place);

Other options:

        // Limit the number of responses
        service.setLimit(5);

        // Limit the search radius when using coordinates
        service.setRadius(50); // km

        // Do not add coordinates to location in return value
        service.setIncludeCoordinates(false);

        // Do not add sunrise and sunset for location in return value
        service.setIncludeSunriseSunset(false);

        // Do not add list of time changes in return value
        service.setIncludeListOfTimeChanges(false);

        // Do not add timezone information to return value
        service.setIncldueTimezoneInformation(false);

Location data type:
--------------------------------------

Get UTC offset for a local time (only applicable if service.setIncludeListOfTimeChanges has been activated):

		TADDateTime localTime = new TADDateTime(2015, 6, 7);
		Location sampleLoc = result.get(0);
		TimeSpan offset = sampleLoc.getUTCOffsetFromLocalTime(localTime);



