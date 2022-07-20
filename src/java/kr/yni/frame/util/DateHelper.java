package kr.yni.frame.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import kr.yni.frame.Constants;
import kr.yni.frame.vo.ValueObject;

/**
 * <p>
 * Date 관련 Framework의 Helper Class
 * </p>
 *
 * <p>
 * 아래 Method들은 모두 <code>static</code>으로 선언 되었기 때문에 유념하기 바란다.
 * </p>
 * 
 * @author YNI-maker
 *
 */
public class DateHelper {

    /**
     * 년
     */
    public static final int YEAR = 1;

    /**
     * 월
     */
    public static final int MONTH = 2;

    /**
     * <p>
     * 일
     * </p>
     */
    public static final int DAY = 3;

    /**
     * <p>
     * 개월
     * </p>
     */
    public static final int MONTH_DAY = 4;

    /**
     * <p>
     * 년/월
     * </p>
     */
    public static final int YEAR_MONTH = 5;

    /**
     * <p>
     * 년/일
     * <p>
     */
    public static final int YEAR_DAY = 6;

    /**
     * <p>
     * 년/월/일
     * </p>
     */
    public static final int YEAR_MONTH_DAY = 7;

    /**
     * 요일의 Korean 표현
     */
    private static String[] koreanWeekStrings = new String[] { "일", "월", "화", "수", "목", "금", "토" };
    private static String[] englishWeekStrings = new String[] {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
    
    /**
     * 초일산입
     */
    public static final int FROM_DAY_ADD = 1;

    /**
     * 말일산입
     */
    public static final int TO_DAY_ADD = 2;

    /**
     * 양편넣기
     */
    public static final int BOTH_DAY_ADD = 3;

    /**
     * 양편빼기
     */
    public static final int NONE_DAY_ADD = 4;
        
    /**
     * <p>
     * <strong>DateHelper</strong>의 default 컨스트럭터(Constructor).
     * </p>
     */
    protected DateHelper() {}

    /**
     * <p>
     * 특정 날짜를 인자로 받아 그 일자로부터 주어진 기간만큼 추가한 날을 계산하여 문자열로 리턴한다.
     * </p>
     *
     * <pre>
     * String result=DateHelper.getCalcDateAsString("2004","10","30",2,"day");
     * </pre>
     *
     * <p>
     * <code>result</code>는 "20041101"의 값을 갖는다.
     * </p>
     *
     * @param sYearPara  년도
     * @param sMonthPara 월
     * @param sDayPara   일
     * @param iTerm      기간
     * @param sGuBun     구분("day":일에 기간을 더함,"month":월에 기간을 더함,"year":년에 기간을 더함.)
     * @return "년+월+일"
     */
    public static String getCalcDateAsString(String sYearPara, String sMonthPara,
            String sDayPara, int iTerm, String sGuBun) {
    	return getCalcDateAsString(sYearPara, sMonthPara, sDayPara, iTerm, sGuBun, null);
    }
    
    /**
	 * <p>
	 * 특정 날짜를 인자로 받아 그 일자로부터 주어진 기간만큼 추가한 날을 계산하여 문자열로 리턴한다.
	 * </p>
	 * 
	 * <pre>
	 * String result = DateHelper.getCalcDateAsString(&quot;2004&quot;, &quot;10&quot;, &quot;30&quot;, 2, &quot;day&quot;);
	 * </pre>
	 * 
	 * <p>
	 * <code>result</code>는 "20041101"의 값을 갖는다.
	 * </p>
	 * 
	 * @param sYearPara
	 *            년도
	 * @param sMonthPara
	 *            월
	 * @param sDayPara
	 *            일
	 * @param iTerm
	 *            기간
	 * @param sGuBun
	 *            구분("day":일에 기간을 더함,"month":월에 기간을 더함,"year":년에 기간을 더함.)
	 * @param sRest
	 *            리턴할 날짜구분("day":날짜만 리턴, "month":월만 리턴, "year":년만 리턴, null:년월일
	 *            리턴)
	 * @return "년+월+일"
	 */
    public static String getCalcDateAsString(String sYearPara, String sMonthPara,
                                             String sDayPara, int iTerm, String sGuBun, String sRest) {

        Calendar cd = new GregorianCalendar(Integer.parseInt(sYearPara),
                Integer.parseInt(sMonthPara) - 1, Integer.parseInt(sDayPara));

        if (StringHelper.strEquals(sGuBun, "day")) {
            cd.add(Calendar.DATE, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "month")) {
            cd.add(Calendar.MONTH, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "year")) {
            cd.add(Calendar.YEAR, iTerm);
        }
        
        String result = null;

		if ("day".equals(sRest)) {
			result = getFormalDay(cd);
		} else if ("month".equals(sRest)) {
			result = getFormalMonth(cd);
		} else if ("year".equals(sRest)) {
			result = getFormalYear(cd);
		} else {
			result = getFormalYear(cd) + getFormalMonth(cd) + getFormalDay(cd);
		}
		
		return result;
    }


    /**
     * <p>
     * 특정 날짜를 인자로 받아 그 일자로부터 주어진 기간만큼 추가한 날을 계산하여 년을 리턴한다.
     * </p>
     *
     * <pre>
     * String result=DateHelper.getCalcYearAsString("2004","12","30",2,"day");
     * <pre>
     *
     * <p>
     * <code>result</code>는 "2005"의 값을 갖는다.
     * </p>
     *
     * @param sYearPara  년도
     * @param sMonthPara 월
     * @param sDayPara   일
     * @param iTerm      기간
     * @param sGuBun     구분("day":일에 기간을 더함,"month":월에 기간을 더함,"year":년에 기간을 더함.)
     * @return 년(年)
     */
    public static String getCalcYearAsString(String sYearPara, String sMonthPara,
                                             String sDayPara, int iTerm, String sGuBun) {

        Calendar cd = new GregorianCalendar(Integer.parseInt(sYearPara),
                Integer.parseInt(sMonthPara) - 1,
                Integer.parseInt(sDayPara));

        if (StringHelper.strEquals(sGuBun, "day")) {
            cd.add(Calendar.DATE, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "month")) {
            cd.add(Calendar.MONTH, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "year")) {
            cd.add(Calendar.YEAR, iTerm);
        }
        return getFormalYear(cd);
    }

    /**
     * <p>
     * 특정 날짜를 인자로 받아 그 일자로부터 주어진 기간만큼 추가한 날을 계산하여 월을 리턴한다.
     * </p>
     *
     * <pre>
     * String result=DateHelper.getCalcMonthAsString("2004","12","30",2,"day");
     * </pre>
     *
     * <p>
     * <code>result</code>는 "01"의 값을 갖는다.
     * </p>
     *
     * @param sYearPara  년도
     * @param sMonthPara 월
     * @param sDayPara   일
     * @param iTerm      기간
     * @param sGuBun     구분("day":일에 기간을 더함,"month":월에 기간을 더함,"year":년에 기간을 더함.)
     * @return 월(月)
     */
    public static String getCalcMonthAsString(String sYearPara, String sMonthPara,
                                              String sDayPara, int iTerm, String sGuBun) {

        Calendar cd = new GregorianCalendar(Integer.parseInt(sYearPara),
                Integer.parseInt(sMonthPara) - 1,
                Integer.parseInt(sDayPara));

        if (StringHelper.strEquals(sGuBun, "day")) {
            cd.add(Calendar.DATE, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "month")) {
            cd.add(Calendar.MONTH, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "year")) {
            cd.add(Calendar.YEAR, iTerm);
        }

        return getFormalMonth(cd);
    }

    /**
     * <p>
     * 특정 날짜를 인자로 받아 그 일자로부터 주어진 기간만큼 추가한 날을 계산하여 일을 리턴한다.
     * </p>
     *
     * <pre>
     * String result=DateHelper.getCalcDayAsString("2004","12","30",3,"day");
     * </pre>
     *
     * <p>
     * <code>result</code>는 "02"의 값을 갖는다.
     * </p>
     *
     * @param sYearPara  년도
     * @param sMonthPara 월
     * @param sDayPara   일
     * @param iTerm      기간
     * @param sGuBun     구분("day":일에 기간을 더함,"month":월에 기간을 더함,"year":년에 기간을 더함.)
     * @return 일(日)
     */
    public static String getCalcDayAsString(String sYearPara, String sMonthPara,
                                            String sDayPara, int iTerm, String sGuBun) {

        Calendar cd = new GregorianCalendar(Integer.parseInt(sYearPara),
                Integer.parseInt(sMonthPara) - 1,
                Integer.parseInt(sDayPara));

        if (StringHelper.strEquals(sGuBun, "day")) {
            cd.add(Calendar.DATE, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "month")) {
            cd.add(Calendar.MONTH, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "year")) {
            cd.add(Calendar.YEAR, iTerm);
        }

        return getFormalDay(cd);
    }


    /**
     * <p>
     * 특정 날짜를 인자로 받아 그 일자로부터 주어진 기간만큼 추가한 날을 계산하여 년을 리턴한다.
     * </p>
     *
     * <pre>
     * int result=DateHelper.getCalcYearAsInt("2004","12","30",3,"day");
     * </pre>
     *
     * <p>
     * <code>result</code>는 2005의 값을 갖는다.
     * </p>
     *
     * @param sYearPara  년도
     * @param sMonthPara 월
     * @param sDayPara   일
     * @param iTerm      기간
     * @param sGuBun     구분("day":일에 기간을 더함,"month":월에 기간을 더함,"year":년에 기간을 더함.)
     * @return 년(年)
     */
    public static int getCalcYearAsInt(String sYearPara, String sMonthPara,
                                       String sDayPara, int iTerm, String sGuBun) {

        Calendar cd = new GregorianCalendar(Integer.parseInt(sYearPara),
                Integer.parseInt(sMonthPara) - 1,
                Integer.parseInt(sDayPara));

        if (StringHelper.strEquals(sGuBun, "day")) {
            cd.add(Calendar.DATE, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "month")) {
            cd.add(Calendar.MONTH, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "year")) {
            cd.add(Calendar.YEAR, iTerm);
        }

        return cd.get(Calendar.YEAR);
    }

    /**
     * <p>
     * 특정 날짜를 인자로 받아 그 일자로부터 주어일 기간만큼 추가한 날을 계산하여 월을 리턴한다.
     * </p>
     *
     * <pre>
     * int result=DateHelper.getCalcMonthAsInt("2004","12","30",3,"day");
     * </pre>
     *
     * <p>
     * <code>result</code>는 1의 값을 갖는다.
     * </p>
     *
     * @param sYearPara  년도
     * @param sMonthPara 월
     * @param sDayPara   일
     * @param iTerm      기간
     * @param sGuBun     구분("day":일에 기간을 더함,"month":월에 기간을 더함,"year":년에 기간을 더함.)
     * @return 월(月)
     */
    public static int getCalcMonthAsInt(String sYearPara, String sMonthPara,
                                        String sDayPara, int iTerm, String sGuBun) {

        Calendar cd = new GregorianCalendar(Integer.parseInt(sYearPara),
                Integer.parseInt(sMonthPara) - 1,
                Integer.parseInt(sDayPara));

        if (StringHelper.strEquals(sGuBun, "day")) {
            cd.add(Calendar.DATE, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "month")) {
            cd.add(Calendar.MONTH, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "year")) {
            cd.add(Calendar.YEAR, iTerm);
        }

        return cd.get(Calendar.MONTH) + 1;
    }

    /**
     * <p>
     * 특정 날짜를 인자로 받아 그 일자로부터 주어일 기간만큼 추가한 날을 계산하여 일을 리턴한다.
     * </p>
     *
     * <pre>
     * int result=DateHelper.getCalcDayAsInt("2004","12","30",3,"day");
     * </pre>
     *
     * <p>
     * <code>result</code>는 2의 값을 갖는다.
     * <p>
     *
     * @param sYearPara  년도
     * @param sMonthPara 월
     * @param sDayPara   일
     * @param iTerm      기간
     * @param sGuBun     구분("day":일에 기간을 더함,"month":월에 기간을 더함,"year":년에 기간을 더함.)
     * @return 일(日)
     */
    public static int getCalcDayAsInt(String sYearPara, String sMonthPara,
                                      String sDayPara, int iTerm, String sGuBun) {

        Calendar cd = new GregorianCalendar(Integer.parseInt(sYearPara),
                Integer.parseInt(sMonthPara) - 1,
                Integer.parseInt(sDayPara));

        if (StringHelper.strEquals(sGuBun, "day")) {
            cd.add(Calendar.DATE, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "month")) {
            cd.add(Calendar.MONTH, iTerm);
        } else if (StringHelper.strEquals(sGuBun, "year")) {
            cd.add(Calendar.YEAR, iTerm);
        }

        return cd.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * <p>
     * 현재 연도값을 리턴
     * </p>
     *
     * @return 년(年)
     */
    public static int getCurrentYearAsInt() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return cd.get(Calendar.YEAR);
    }

    /**
     * <p>
     * 현재 월을 리턴
     * </p>
     *
     * @return 월(月)
     */
    public static int getCurrentMonthAsInt() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return cd.get(Calendar.MONTH) + 1;
    }

    /**
     * <p>
     * 현재 일을 리턴
     * </p>
     *
     * @return 일(日)
     */
    public static int getCurrentDayAsInt() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return cd.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * <p>
     * 현재 시간을 리턴
     * </p>
     *
     * @return 시(時)
     */
    public static int getCurrentHourAsInt() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return cd.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * <p>
     * 현재 분을 리턴
     * </p>
     *
     * @return 분(分)
     */
    public static int getCurrentMinuteAsInt() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return cd.get(Calendar.MINUTE);
    }

    /**
     * <p>
     * 현재 초를 리턴
     * </p>
     *
     * @return 밀리초
     */
    public static int getCurrentMilliSecondAsInt() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return cd.get(Calendar.MILLISECOND);
    }

    /**
     * <p>
     * 현재 년도를 YYYY 형태로 리턴
     * </p>
     *
     * @return 년도(YYYY)
     */
    public static String getCurrentYearAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalYear(cd);
    }

    /**
     * <P>
     * 현재 월을 MM 형태로 리턴
     * </p>
     *
     * @return 월(MM)
     */
    public static String getCurrentMonthAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalMonth(cd);
    }

    /**
     * <p>
     * 현재 일을 DD 형태로 리턴
     * </p>
     *
     * @return 일(DD)
     */
    public static String getCurrentDayAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalDay(cd);
    }

    /**
     * <p>
     * 현재 시간을 HH 형태로 리턴
     * </p>
     *
     * @return 시간(HH)
     */
    public static String getCurrentHourAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalHour(cd);
    }

    /**
     * <p>
     * 현재 분을 mm 형태로 리턴
     * </p>
     *
     * @return 분(mm)
     */
    public static String getCurrentMinuteAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalMin(cd);
    }

    /**
     * <p>
     * 현재 초를 ss 형태로 리턴
     * </p>
     *
     * @return 초(ss)
     */
    public static String getCurrentSecondAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalSec(cd);
    }

    /**
     * <p>
     * 현재 밀리초를 sss 형태로 리턴
     * </p>
     *
     * @return 밀리초(sss)
     */
    public static String getCurrentMilliSecondAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalMSec(cd);
    }

    /**
     * <p>
     * 현재 날짜를 년월일을 합쳐서 String으로 리턴하는 메소드
     * </p>
     *
     * @return 년+월+일 값
     */
    public static String getCurrentDateAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalYear(cd) + getFormalMonth(cd) + getFormalDay(cd);
    }

    /**
     * <p>
     * 현재 시간을 시분초를 합쳐서 String으로 리턴하는 메소드
     * </p>
     *
     * @return 시+분+초 값
     */
    public static String getCurrentTimeAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalHour(cd) + getFormalMin(cd) + getFormalSec(cd);
    }

    /**
     * <p>
     * 현재 날짜와 시간을 년월일시분초를 합쳐서 String으로 리턴하는 메소드
     * </p>
     *
     * @return 년+월+일+시+분+초 값
     */
    public static String getCurrentDateTimeAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalYear(cd) + getFormalMonth(cd) + getFormalDay(cd) + getFormalHour(cd) + getFormalMin(cd) + getFormalSec(cd);
    }

    /**
     * <p>
     * 현재 날짜와 시간을 년월일시분초밀리초를 합쳐서 String으로 리턴하는 메소드
     * </p>
     *
     * @return 년+월+일+시+분+초+밀리초 값
     */
    public static String getCurrentDateTimeMilliSecondAsString() {

        Calendar cd = new GregorianCalendar(Locale.KOREA);

        return getFormalYear(cd) + getFormalMonth(cd) + getFormalDay(cd) + getFormalHour(cd) + getFormalMin(cd) + getFormalSec(cd) + getFormalMSec(cd);
    }

    /**
     * <p>
     * 해당 년,월,일을 받아 요일을 리턴하는 메소드
     * </p>
     *
     * @param sYear  년도
     * @param sMonth 월
     * @param sDay   일
     * @return 요일(한글)
     */
    public static String getDayOfWeekAsString(String sYear, String sMonth, String sDay) {

        Calendar cd = new GregorianCalendar(Integer.parseInt(sYear),
                Integer.parseInt(sMonth) - 1,
                Integer.parseInt(sDay));

        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.KOREA); // "EEE" - Day in Week

        Date d1 = cd.getTime();

        return sdf.format(d1);
    }


    /**
     * <p>
     * 해당 대상자에 대해 기준일자에서의 만 나이를 구한다.
     * </p>
     * <pre>
     * 주민번호의 7번째 Flag
     * - 0,9 일 경우 : 내국인 1800년도 출생
     * - 1,2 일 경우 : 내국인 1900년도 출생
     * - 3,4 일 경우 : 내국인 2000년도 출생
     * - 5,6 일 경우 : 외국인 1900년도 출생
     * - 7,8 일 경우 : 외국인 2000년도 출생
     * </pre>
     *
     * <pre>
     * int age = DateHelper.getFullAge("7701011234567","20041021");
     * </pre>
     *
     * <p>
     * <code>age</code>는 27의 값을 갖는다.
     * </p>
     *
     * @param socialNo 주민번호 13자리
     * @param keyDate  기준일자 8자리
     * @return 만 나이
     */
    public static int getFullAge(String socialNo, String keyDate) {

        String birthDate = null;

        birthDate = getSocialNo2DateFormat(socialNo);

        return getFullAgeByBirthDate(birthDate,keyDate);
    }

    /**
     * 생년월일(yyyymmdd)과 기준일(yyyymmdd)을 주면 만 나이를 구하여 리턴한다. 
     * @param birthDay
     * @param keyDate
     * @return
     */
    public static int getFullAgeByBirthDate(String birthDate, String keyDate) {
        //생일이 안지났을때 기준일자 년에서 생일년을 빼고 1년을 더뺀다.
        if (Integer.parseInt(keyDate.substring(4, 8)) <
                Integer.parseInt(birthDate.substring(4, 8))) {

            return Integer.parseInt(keyDate.substring(0, 4)) -
                    Integer.parseInt(birthDate.substring(0, 4)) - 1;
        } else { //생일이 지났을때 기준일자 년에서 생일년을 뺀다.
            return Integer.parseInt(keyDate.substring(0, 4)) -
                    Integer.parseInt(birthDate.substring(0, 4));
        }
    }
    
    /**
     * <p>
     * 주민번호를 출생 년월일로 표현한다.
     * </p>
     *
     * <pre>
     * 주민번호의 7번째 Flag
     * - 0,9 일 경우 : 내국인 1800년도 출생
     * - 1,2 일 경우 : 내국인 1900년도 출생
     * - 3,4 일 경우 : 내국인 2000년도 출생
     * - 5,6 일 경우 : 외국인 1900년도 출생
     * - 7,8 일 경우 : 외국인 2000년도 출생
     * </pre>
     *
     * @param socialNo 주민번호
     * @return 년월일 8자리 [yyyyMMdd]
     */
    public static String getSocialNo2DateFormat(String socialNo) {

        String birthDate = null;

        if (StringHelper.null2void(socialNo).length() < 13) {
            throw new IllegalArgumentException(" place possibility must be an above 6 places.(socialNo)");
        }

        // 주민번호 7번째 자리가 0 또는 9 라면 1800년도 출생이다.
        if (StringHelper.strEquals(StringHelper.toSubString(socialNo, 6, 7), "0") ||
                StringHelper.strEquals(StringHelper.toSubString(socialNo, 6, 7), "9")) {
            birthDate = "18" + socialNo.substring(0, 6);
        }

        // 주민번호 7번째 자리가 1 또는 2 라면 1900년도 출생이다.
        // 2008.09.25. 김기호 추가
        // 주민번호 7번째 자리가 5 또는 6 이라면 1900년도 출생 외국인이다.
        else if (StringHelper.strEquals(StringHelper.toSubString(socialNo, 6, 7), "1") ||
                StringHelper.strEquals(StringHelper.toSubString(socialNo, 6, 7), "2") ||
                StringHelper.strEquals(StringHelper.toSubString(socialNo, 6, 7), "5") ||
                StringHelper.strEquals(StringHelper.toSubString(socialNo, 6, 7), "6")) {
            birthDate = "19" + socialNo.substring(0, 6);
        }

        // 주민번호 7번째 자리가 3 또는 4 라면 2000년도 출생이다.
        // 2008.09.25. 김기호 추가
        // 주민번호 7번째 자리가 7 또는 8 이라면 2000년도 출생 외국인이다.
        else if (StringHelper.strEquals(StringHelper.toSubString(socialNo, 6, 7), "3") ||
                StringHelper.strEquals(StringHelper.toSubString(socialNo, 6, 7), "4") ||
                StringHelper.strEquals(StringHelper.toSubString(socialNo, 6, 7), "7") ||
                StringHelper.strEquals(StringHelper.toSubString(socialNo, 6, 7), "8")) {
            birthDate = "20" + socialNo.substring(0, 6);
        } else {
            throw new IllegalArgumentException(" wrong flag [7].");
        }

        return birthDate;

    }

    /**
     * <p>
     * 주민번호를 넘겨 현재 시점의 만 나이를 구한다.
     * </p>
     * <pre>
     * 주민번호의 7번째 Flag
     * - 0,9 일 경우 : 내국인 1800년도 출생
     * - 1,2 일 경우 : 내국인 1900년도 출생
     * - 3,4 일 경우 : 내국인 2000년도 출생
     * - 5,6 일 경우 : 외국인 1900년도 출생
     * - 7,8 일 경우 : 외국인 2000년도 출생
     * </pre>
     *
     * @param socialNo 주민번호 13자리
     * @return 만 나이
     */
    public static int getCurrentFullAge(String socialNo) {

        //현재일자를 구한다.
        String sCurrentDate = getCurrentYearAsString() + getCurrentMonthAsString() +
                getCurrentDayAsString();

        return getFullAge(socialNo, sCurrentDate);
    }

    /**
     * <p>
     * 해당 년의 특정월의 마지막 일자를 구한다.
     * </p>
     *
     * @param year  년도4자리
     * @param month 월 1자리 또는 2자리
     * @return 특정월의 마지막 일자
     */
    public static int getDayCountForMonth(int year, int month) {

        int[] DOMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};  // 평년
        int[] lDOMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};  // 윤년

        if (isLeapYear(year)) {
            return lDOMonth[month - 1];
        } else {
            return DOMonth[month - 1];
        }

    }

    /**
     * <p>
     * 윤달 여부 확인.
     * </p>
     *
     * @param year 연도(String)
     * @return 윤달이면 true
     */
    public static boolean isLeapYear(String year) {
        return new GregorianCalendar().isLeapYear(Integer.parseInt(year));
    }

    /**
     * <p>
     * 윤달 여부 확인.
     * </p>
     *
     * @param year 연도(int)
     * @return 윤달이면 true
     */
    public static boolean isLeapYear(int year) {
        return new GregorianCalendar().isLeapYear(year);
    }

    //****** 시작일자와 종료일자 사이의 일자를 구하는 메소드군 *******//

    /**
     * <p>
     * 8자리로된(yyyyMMdd) 시작일자와 종료일자 사이의 일수를 구함.
     * </p>
     *
     * @param from 8자리로된(yyyyMMdd)시작일자
     * @param to   8자리로된(yyyyMMdd)종료일자
     * @return 날짜 형식이 맞고, 존재하는 날짜일 때 2개 일자 사이의 일수 리턴
     * @throws ParseException 형식이 잘못 되었거나 존재하지 않는 날짜인 경우 발생함
     */
    public static int getDayCount(String from, String to) throws ParseException {

        return getDayCountWithFormatter(from, to, "yyyyMMdd");
    }

    /**
     * <p>
     * 해당 문자열이 "yyyyMMdd" 형식에 합당한지 여부를 판단하여 합당하면 Date 객체를 리턴한다.
     * </p>
     *
     * @param source 대상 문자열
     * @return "yyyyMMdd" 형식에 맞는 Date 객체를 리턴한다.
     * @throws ParseException 형식이 잘못 되었거나 존재하지 않는 날짜인 경우 발생함
     */
    public static Date dateFormatCheck(String source) throws ParseException {

        return dateFormatCheck(source, "yyyyMMdd");
    }

    /**
     * <p>
     * 해당 문자열이 주어진 일자 형식을 준수하는지 여부를 검사한다.
     * </p>
     *
     * @param source 검사할 대상 문자열
     * @param format Date 형식의 표현. 예) "yyyy-MM-dd".
     * @return 형식에 합당하는 경우 Date 객체를 리턴한다.
     * @throws ParseException 형식이 잘못 되었거나 존재하지 않는 날짜인 경우 발생함
     */
    public static Date dateFormatCheck(String source, String format) throws ParseException {

        if (source == null) {
            throw new ParseException("date string to check is null", 0);
        }

        if (format == null) {
            throw new ParseException("format string to check date is null", 0);
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);

        Date date = null;

        try {
            date = formatter.parse(source);
        } catch (ParseException e) {
            throw new ParseException(" wrong date:\"" + source +
                    "\" with format \"" + format + "\"", 0);
        }

        if (!formatter.format(date).equals(source)) {
            throw new ParseException("Out of bound date:\"" + source +
                    "\" with format \"" + format + "\"", 0);
        }

        return date;
    }

    /**
     * <p>
     * 정해진 일자 형식을 기반으로 시작일자와 종료일자 사이의 일자를 구한다.
     * <p/>
     *
     * @param from 시작 일자
     * @param to   종료 일자
     * @return 날짜 형식이 맞고, 존재하는 날짜일 때 2개 일자 사이의 일수를 리턴
     * @throws ParseException 형식이 잘못 되었거나 존재하지 않는 날짜인 경우 발생함
     * @see #getTimeCount(String, String, String)
     */
    public static int getDayCountWithFormatter(String from, String to, String format) throws ParseException {

        long duration = getTimeCount(from, to, format);

        return (int) (duration / (1000 * 60 * 60 * 24));
    }

    /**
     * <p>
     * DATE 문자열을 이용한 format string을 생성
     * </p>
     *
     * @param date Date 문자열
     * @return Java.text.DateFormat 부분의 정규 표현 문자열
     * @throws ParseException 형식이 잘못 되었거나 존재하지 않는 날짜인 경우 발생함
     */
    protected static String getFormatStringWithDate(String date) throws ParseException {
        String format = null;

        if (date.length() == 4) {
            format = "HHmm";
        } else if (date.length() == 8) {
            format = "yyyyMMdd";
        } else if (date.length() == 10) {
        	format = "yyyyMMddHH";
        } else if (date.length() == 12) {
            format = "yyyyMMddHHmm";
        } else if (date.length() == 14) {
            format = "yyyyMMddHHmmss";
        } else if (date.length() == 17) {
            format = "yyyyMMddHHmmssSSS";
        } else {
            throw new ParseException(" wrong date format!:\"" + format + "\"", 0);
        }

        return format;
    }

    /**
     * <p>
     * <code>yyyyMMddHHmmssSSS</code> 와 같은 Format의 문자열로 시작 일자 시간, 끝 일자 시간을 주면
     * 두 시간의 차이를 밀리초 값(long)으로 반환한다.
     * </p>
     *
     * @param from 시작일자
     * @param to   끝일자
     * @return 두 일자 간의 차의 밀리초(long)값
     * @throws ParseException 형식이 잘못 되었거나 존재하지 않는 날짜인 경우 발생함
     * @see #getFormatStringWithDate(String)
     */
    public static long getTimeCount(String from, String to) throws ParseException {

        String format = getFormatStringWithDate(from);

        return getTimeCount(from, to, format);
    }

    /**
     * <p>
     * 정해진 일자 형식을 기반으로 시작일자와 종료일자 사이의 일자를 구한다.
     * <p/>
     *
     * <pre>
     * Symbol   Meaning                 Presentation        Example
     * ------   -------                 ------------        -------
     * G        era designator          (Text)              AD
     * y        year                    (Number)            1996
     * M        month in year           (Text & Number)     July & 07
     * d        day in month            (Number)            10
     * h        hour in am/pm (1~12)    w(Number)            12
     * H        hour in day (0~23)      (Number)            0
     * m        minute in hour          (Number)            30
     * s        second in minute        (Number)            55
     * S        millisecond             (Number)            978
     * E        day in week             (Text)              Tuesday
     * D        day in year             (Number)            189
     * F        day of week in month    (Number)            2 (2nd Wed in July)
     * w        week in year            (Number)            27
     * W        week in month           (Number)            2
     * a        am/pm marker            (Text)              PM
     * k        hour in day (1~24)      (Number)            24
     * K        hour in am/pm (0~11)    (Number)            0
     * z        time zone               (Text)              Pacific Standard Time
     * '        escape for text         (Delimiter)
     * ''       single quote            (Literal)           '
     * </pre>
     *
     * @param from   시작 일자
     * @param to     종료 일자
     * @param format
     * @return 날짜 형식이 맞고, 존재하는 날짜일 때 2개 일자 사이의 밀리초를 리턴
     * @throws ParseException 형식이 잘못 되었거나 존재하지 않는 날짜인 경우 발생함
     */
    public static long getTimeCount(String from, String to, String format) throws ParseException {

        Date d1 = dateFormatCheck(from, format);
        Date d2 = dateFormatCheck(to, format);

        long duration = d2.getTime() - d1.getTime();

        return duration;
    }


    /**
     * <p>
     * 시작일자와 종료일자 사이의 해당 요일이 몇번 있는지 계산한다.
     * </p>
     *
     * @param from 시작 일자
     * @param to   종료 일자
     * @param yoil 요일
     * @return 날짜 형식이 맞고, 존재하는 날짜일 때 2개 일자 사이의 일자 리턴
     * @throws ParseException 발생. 형식이 잘못 되었거나 존재하지 않는 날짜
     */
    public static int getDayOfWeekCount(String from, String to, String yoil) throws ParseException {

        int first = 0; // from 날짜로 부터 며칠 후에 해당 요일인지에 대한 변수
        int count = 0; // 해당 요일이 반복된 횟수
        String[] sYoil = {"일", "월", "화", "수", "목", "금", "토"};

        // 두 일자 사이의 날 수
        int betweenDays = getDayCount(from, to);

        // 첫번째 일자에 대한 요일
        Calendar cd = new GregorianCalendar(Integer.parseInt(from.substring(0, 4)),
                Integer.parseInt(from.substring(4, 6)) - 1,
                Integer.parseInt(from.substring(6, 8)));
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);

        // 요일이 3자리이면 첫자리만 취한다.
        if (yoil.length() == 3) {
            yoil = yoil.substring(0, 1);

            // 첫번째 해당 요일을 찾는다.
        }
        while (!sYoil[dayOfWeek - 1].equals(yoil)) {
            dayOfWeek += 1;
            first++;
        }

        if ((betweenDays - first) < 0) {

            return 0;

        } else {

            count++;

        }
        count += (betweenDays - first) / 7;

        return count;
    }


    /**
     * <p>
     * 년도 표시를 네자리로 형식화 한다.
     * </p>
     *
     * @param cd 년도를 포함하는 <strong>Calendar</strong> 오브젝트
     * @return 네자리로 형식화된 년도
     */
    private static String getFormalYear(Calendar cd) {
        return toString(cd.getTime(), "yyyy", Locale.KOREA);
    }


    /**
     * <p>
     * 월(Month) 표시를 두자리로 형식화 한다.
     * </p>
     *
     * @param cd 월을 포함하는 <strong>Calendar</strong> 오브젝트
     * @return 두자리로 형식화된 월
     */
    private static String getFormalMonth(Calendar cd) {

        return toString(cd.getTime(), "MM", Locale.KOREA);
        
    }

    /**
     * <p>
     * 일(Day) 표시를 두자리로 형식화 한다.
     * </p>
     *
     * @param cd 일자를 포함하는 <strong>Calendar</strong> 오브젝트
     * @return 두자리로 형식화된 일
     */
    private static String getFormalDay(Calendar cd) {

        return toString(cd.getTime(), "dd", Locale.KOREA);
        
    }

    /**
     * <p>
     * 시간(Hour) 표시를 두자리로 형식화 한다.
     * </p>
     *
     * @param cd 시간을 포함하는 <strong>Calendar</strong> 오브젝트
     * @return 두자리로 형식화된 시간
     */
    private static String getFormalHour(Calendar cd) {

        return toString(cd.getTime(), "HH", Locale.KOREA);

    }

    /**
     * <p>
     * 분(Minute) 표시를 두자리로 형식화 한다.
     * </p>
     *
     * @param cd 분을 포함하는 <strong>Calendar</strong> 오브젝트
     * @return 두자리로 형식화된 분
     */
    private static String getFormalMin(Calendar cd) {
    	
        return toString(cd.getTime(), "mm", Locale.KOREA);
        
    }

    /**
     * <p>
     * 초(sec) 표시를 두자리로 형식화 한다.
     * </p>
     *
     * @param cd 초를 포함하는 <strong>Calendar</strong> 오브젝트
     * @return 두자리로 형식화된 초
     */
    private static String getFormalSec(Calendar cd) {

        return toString(cd.getTime(), "ss", Locale.KOREA);

    }

    /**
     * <p>
     * 밀리초(millisec) 표시를 세자리로 형식화 한다.
     * </p>
     *
     * @param cd 밀리초를 포함하는 <strong>Calendar</strong> 오브젝트
     * @return 세자리로 형식화된 밀리초
     */
    private static String getFormalMSec(Calendar cd) {

        return toString(cd.getTime(), "SSS", Locale.KOREA);
        
    }

    /**
     * <p>
     * Date -> String
     * </p>
     *
     * @param date Date which you want to change.
     * @return String The Date string. Type,  yyyyMMdd HH:mm:ss.
     */
    public static String toString(Date date, String format, Locale locale) {

        if (StringHelper.isNull(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        if (locale == null) {
            locale = java.util.Locale.KOREA;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format, locale);

        String tmp = sdf.format(date);

        return tmp;
    }

    /**
     * <p>
     * 날짜형 문자열에 형식(format)을 적용한 문자열을 리턴.
     * </p>
     *
     * <p>
     * 아래 문자열 자리수당 처리 자리수     <br>
     * -------------------------------  <br>
     * 문자열  8자리   : yyyyMMdd        <br>
     * 문자열 12자리   : yyyyMMddHHmm    <br>
     * 문자열 14자리   : yyyyMMddHHmmss  <br>
     * </p>
     *
     * <pre>
     * String source = "20050117";
     * String format = "yyyy년 MM월 dd일";
     * String result = DateHelperExt.formatter(source, format);
     * </pre>
     * <code>result</code>는 <code>"2005년 01월 17일"</code> 을 가지게 된다.
     *
     * @param strDate 문자열 Data
     * @param format  형식(format)
     * @return 완성된 문자열
     */
    public static String formatter(String strDate, String format) {
    	Locale loc = getTimeLocaleForProperties();
    	
        Calendar calendar = Calendar.getInstance(loc);

        if (strDate.length() == 8) {
            calendar.set(Integer.parseInt(strDate.substring(0, 4)), Integer.parseInt(strDate.substring(4, 6)) - 1, Integer.parseInt(strDate.substring(6)));
        } else if (strDate.length() == 12) {
            calendar.set(Integer.parseInt(strDate.substring(0, 4)), Integer.parseInt(strDate.substring(4, 6)) - 1, Integer.parseInt(strDate.substring(6, 8)), Integer.parseInt(strDate.substring(8, 10)), Integer.parseInt(strDate.substring(10)));
        } else if (strDate.length() == 14) {
            calendar.set(Integer.parseInt(strDate.substring(0, 4)), Integer.parseInt(strDate.substring(4, 6)) - 1, Integer.parseInt(strDate.substring(6, 8)), Integer.parseInt(strDate.substring(8, 10)), Integer.parseInt(strDate.substring(10, 12)), Integer.parseInt(strDate.substring(12)));
        } else {
            return strDate;
        }

        //return new SimpleDateFormat(format).format(calendar.getTime());

        return toString(calendar.getTime(), format, loc);
    }
    
    /**
     * 시스템설정에 지정된 일자 및 시간 셋팅에 따라 Locale 획득(없으면 Locale.KOREA임)
     * <br>CANADA
 	 * <br>CANADA_FRENCH
	 * <br>CHINA
	 * <br>CHINESE
	 * <br>ENGLISH
	 * <br>FRANCE
	 * <br>FRENCH
	 * <br>GERMAN
	 * <br>GERMANY
	 * <br>ITALIAN
	 * <br>ITALY
	 * <br>JAPAN
	 * <br>JAPANESE
	 * <br>PRC
	 * <br>ROOT
	 * <br>TAIWAN
	 * <br>TRADITIONAL_CHINESE
	 * <br>UK
	 * <br>US
     * @return
     */
    public static Locale getTimeLocaleForProperties() {
    	Locale loc = null;
    	
    	if(Constants.APPLICATION_TIME_NATION.equals("CANADA")) loc = Locale.CANADA;
    	else if(Constants.APPLICATION_TIME_NATION.equals("CANADA_FRENCH"))  loc = Locale.CANADA_FRENCH;
    	else if(Constants.APPLICATION_TIME_NATION.equals("CHINA"))  loc = Locale.CHINA;
    	else if(Constants.APPLICATION_TIME_NATION.equals("CHINESE"))  loc = Locale.CHINESE;
    	else if(Constants.APPLICATION_TIME_NATION.equals("ENGLISH"))  loc = Locale.ENGLISH;
    	else if(Constants.APPLICATION_TIME_NATION.equals("FRANCE"))  loc = Locale.FRANCE;
    	else if(Constants.APPLICATION_TIME_NATION.equals("FRENCH"))  loc = Locale.FRENCH;
    	else if(Constants.APPLICATION_TIME_NATION.equals("GERMAN"))  loc = Locale.GERMAN;
    	else if(Constants.APPLICATION_TIME_NATION.equals("GERMANY"))  loc = Locale.GERMANY;
    	else if(Constants.APPLICATION_TIME_NATION.equals("ITALIAN"))  loc = Locale.ITALIAN;
    	else if(Constants.APPLICATION_TIME_NATION.equals("ITALY"))  loc = Locale.ITALY;
    	else if(Constants.APPLICATION_TIME_NATION.equals("JAPAN"))  loc = Locale.JAPAN;
    	else if(Constants.APPLICATION_TIME_NATION.equals("JAPANESE"))  loc = Locale.JAPANESE;
    	else if(Constants.APPLICATION_TIME_NATION.equals("PRC"))  loc = Locale.PRC;
    	else if(Constants.APPLICATION_TIME_NATION.equals("ROOT"))  loc = Locale.ROOT;
    	else if(Constants.APPLICATION_TIME_NATION.equals("TAIWAN"))  loc = Locale.TAIWAN;
    	else if(Constants.APPLICATION_TIME_NATION.equals("TRADITIONAL_CHINESE"))  loc = Locale.TRADITIONAL_CHINESE;
    	else if(Constants.APPLICATION_TIME_NATION.equals("UK"))  loc = Locale.UK;
    	else if(Constants.APPLICATION_TIME_NATION.equals("US"))  loc = Locale.US;
    	else loc = Locale.KOREA;
    	
    	return loc;
    }
    
    /**
     * <p>
     * 주어진 두 날짜(시작일, 기준일) 사이의 기간을 구하여 명시된 형태로 리턴한다.
     * </p>
     *
     * <pre>
     * String from = "19800810";
     * String to = "20050330";
     * String result1 = DateHelper.getDateInterval(from, to, DateHelper.YEAR_MONTH_DAY);
     * String result2 = DateHelper.getDateInterval(from, to, DateHelper.YEAR_MONTH);
     * String result3 = DateHelper.getDateInterval(from, to, DateHelper.YEAR_DAY);
     * String result4 = DateHelper.getDateInterval(from, to, DateHelper.MONTH_DAY);
     * String result5 = DateHelper.getDateInterval(from, to, DateHelper.DAY);
     * String result6 = DateHelper.getDateInterval(from, to, DateHelper.MONTH);
     * String result7 = DateHelper.getDateInterval(from, to, DateHelper.YEAR);
     * </pre>
     * <code> result1</code>은 <code>"24/7/20"</code>의 값을 가지게 된다.<br>
     * <code> result2</code>은 <code>"24/7"</code>의 값을 가지게 된다.<br>
     * <code> result3</code>은 <code>"24/232"</code>의 값을 가지게 된다.<br>
     * <code> result4</code>은 <code>"295/20"</code>의 값을 가지게 된다.<br>
     * <code> result5</code>은 <code>"24/232"</code>의 값을 가지게 된다.<br>
     * <code> result6</code>은 <code>"8998"</code>의 값을 가지게 된다.<br>
     * <code> result7</code>은 <code>"24"</code>의 값을 가지게 된다.<br>
     *
     *
     * <p>-------------------------------------------------------------------<br>
     * ※ 리턴 받은 Data의 Format 변환 예제<br>
     * String result = "24/7/20"; 일 경우<br>
     * String[] tmp = result.split("/");<br>
     * String result1 = tmp[0] + "년" + tmp[1] + "월" + tmp[2] + "일";<br>
     * result1은 "24년7월20일"이 된다.<br>
     * </p>
     *
     * @param from   시작일자로써 "yyyyMMdd"형태의 문자열
     * @param to     기준일자로써 "yyyyMMdd"형태의 문자열
     * @param format 리턴될 기간 문자열 format
     *               <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>년:{@link DateHelper#YEAR}
     *               <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>월:{@link DateHelper#MONTH}
     *               <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>일:{@link DateHelper#DAY}
     *               <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>월/일:{@link DateHelper#MONTH_DAY}
     *               <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>년/월:{@link DateHelper#YEAR_MONTH}
     *               <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>년/일:{@link DateHelper#YEAR_DAY}
     *               <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>년/월/일:{@link DateHelper#YEAR_MONTH_DAY}
     * @return 시작일자와 기준일자 사이의 기간
     * @throws ParseException
     */
    public static String getDateInterval(String from, String to, int format) throws ParseException {

        int dayCount = 0;
        int monthDue = 0, yearDue = 0, dayDue = 0;

        String result = "";

        dayDue = getDayCount(from, to);

        // DAY일 경우
        if (format == DAY) {
            return String.valueOf(dayDue);
        }

        int year = Integer.parseInt(StringHelper.toSubString(from, 0, 4));
        int month = Integer.parseInt(StringHelper.toSubString(from, 4, 6));
        int day = Integer.parseInt(StringHelper.toSubString(from, 6));

        if (day != 1) {
            month += 1;
        }

        do {
            if (month > 12) {
                month = 1;
                year += 1;
            }

            dayCount = getDayCountForMonth(year, month);

            if (dayDue < dayCount) {
                break;
            }

            dayDue = dayDue - dayCount;

            month++;
            monthDue++;

            if (format == YEAR_DAY && dayDue < 365 && (monthDue % 12 == 0)) {
                break;
            }

        } while (true);

        // 년도 계산
        if (format != MONTH_DAY && format != MONTH && monthDue >= 12) {
            yearDue = monthDue / 12;
            monthDue = monthDue % 12;
        }

        if (format == YEAR) {
            result = String.valueOf(yearDue);
        } else if (format == MONTH) {
            result = String.valueOf(monthDue);
        } else if (format == MONTH_DAY) {
            result = monthDue + "/" + dayDue;
        } else if (format == YEAR_MONTH) {
            result = yearDue + "/" + monthDue;
        } else if (format == YEAR_DAY) {
            result = yearDue + "/" + dayDue;
        } else { // format == YEAR_MONTH_DAY
            result = yearDue + "/" + monthDue + "/" + dayDue;
        }

        return result;

    }

    /**
     * "HHmm", "yyyyMMdd", "yyyyMMddHH", "yyyyMMddHHmm", "yyyyMMddHHmmss" 또는 
     * "yyyyMMddHHmmssSSS" 형태의 문자열로 표현된 Date 값을 받아서 그 시간 차이를 
     * "yyyyMMddHHmmssSSS" 형태의 문자열로 반환한다.
     * @param from
     * @param to
     * @return
     * @throws ParseException
     */
	public static String getDateTimeInterval(String from, String to) throws ParseException {
		
		// 입력 문자열의 자릿수를 기준으로 포멧 문자열을 정하여 yyyyMMddHHmmssSSS 형태의 문자열로 통일시킨다.
		String fformat = getFormatStringWithDate(from);
		String tformat = getFormatStringWithDate(to);
		
		Date fdate = dateFormatCheck(from,fformat);
		Date tdate = dateFormatCheck(to,tformat);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		
		from = sdf.format(fdate);
		to = sdf.format(tdate);
		
		// 자릿수대로 잘라서 정수값으로 담아 놓는다.
        int fyear = Integer.parseInt(StringHelper.toSubString(from, 0, 4));
        int fmonth = Integer.parseInt(StringHelper.toSubString(from, 4, 6));
        int fday = Integer.parseInt(StringHelper.toSubString(from, 6, 8));
        int fhour = Integer.parseInt(StringHelper.toSubString(from, 8, 10));
        int fmin = Integer.parseInt(StringHelper.toSubString(from, 10, 12));
        int fsec = Integer.parseInt(StringHelper.toSubString(from, 12, 14));
        int fmsec = Integer.parseInt(StringHelper.toSubString(from, 14));

        int tyear = Integer.parseInt(StringHelper.toSubString(to, 0, 4));
        int tmonth = Integer.parseInt(StringHelper.toSubString(to, 4, 6));
        int tday = Integer.parseInt(StringHelper.toSubString(to, 6, 8));
        int thour = Integer.parseInt(StringHelper.toSubString(to, 8, 10));
        int tmin = Integer.parseInt(StringHelper.toSubString(to, 10, 12));
        int tsec = Integer.parseInt(StringHelper.toSubString(to, 12, 14));
        int tmsec = Integer.parseInt(StringHelper.toSubString(to, 14));
        
        // msec 차이 계산
        int emsec = tmsec - fmsec;
        if (emsec < 0) {
        	emsec += 1000;
        	tsec--;
        }
        // sec 차이 계산
        int esec = tsec - fsec;
        if (esec < 0) {
        	esec += 60;
        	tmin--;
        }
        
        // min 차이 계산
        int emin = tmin - fmin;
        if (emin < 0) {
        	emin += 60;
        	thour--;
        }
        
        // hour 차이 계산
        int ehour = thour - fhour;
        if (ehour < 0) {
        	ehour += 24;
        	tday--;
        }
        
        // day 차이 계산, 음수가 되면 from 월의 최대 날짜 만큼 증가시기코 from 월을 하나 증가시킨다.
        int fmaxdays = DateHelper.getDayCountForMonth(fyear,fmonth);
        int eday = tday - fday;
        if (eday < 0) {
        	eday += fmaxdays;
        	fmonth++;
        }
        if (fmonth > 12) {
        	fmonth = 1;
        	fyear++;
        }
        
        // month 차이 계산
        int emonth = tmonth - fmonth;
        if (emonth < 0) {
        	emonth += 12;
        	tyear--;
        }
        
        // year 차이 계산
        int eyear = tyear - fyear;
        
        // 문자열로 생성한다.
        StringBuilder sb = new StringBuilder(17);
        sb.append(StringHelper.lPad(Integer.toString(eyear), 4, '0'));
        sb.append(StringHelper.lPad(Integer.toString(emonth), 2, '0'));
        sb.append(StringHelper.lPad(Integer.toString(eday), 2, '0'));
        sb.append(StringHelper.lPad(Integer.toString(ehour), 2, '0'));
        sb.append(StringHelper.lPad(Integer.toString(emin), 2, '0'));
        sb.append(StringHelper.lPad(Integer.toString(esec), 2, '0'));
        sb.append(StringHelper.lPad(Integer.toString(emsec), 3, '0'));
       
        return sb.toString();
	}
	
    /**
     * <p>
     * 주어진 두 날짜(시작일, 기준일) 사이의 기간을 구하여 년,월,일 형태로 리턴한다.
     * </p>
     *
     * <pre>
     * String from = "19800810";
     * String to = "20050330";
     * String result1 = DateHelper.getDateInterval(from, to);
     * </pre>
     * <code> result1</code>은 <code>"24/7/20"</code>의 값을 가지게 된다.<br>
     *
     * @param from 시작일자로써 "yyyyMMdd"형태의 문자열
     * @param to   기준일자로써 "yyyyMMdd"형태의 문자열
     * @return 시작일자와 기준일자 사이의 기간
     * @throws ParseException
     */
    public static String getDateInterval(String from, String to) throws ParseException {
        return getDateInterval(from, to, DateHelper.YEAR_MONTH_DAY);
    }

    /**
     * 일자(yyyymmdd)를 입력받아 마지막 일을 dd 타입으로 리턴
     *
     * @param day String yyyymmdd
     * @return String 일
     */
    public static String getMaxDayOfMonth(String day) {
      Calendar cal = stringToCalendar(day);
      return StringHelper.lPad(Integer.toString(cal.getActualMaximum(Calendar.DAY_OF_MONTH)), 2, '0');
    }

    /**
     * 일자(yyyymmdd)를 입력받아 마지막 일자를 yyyymmdd 형태로 리턴
     *
     * @param day String yyyymmdd
     * @return String yyyymmdd
     */
    public static String getMaxDay(String day) {
      return day.substring(0,6) + getMaxDayOfMonth(day);
    }

    /**
     * 서버의 현재일자를 yyyymmdd 형식의 스트링으로 리턴
     *
     * @return String yyyymmdd
     */
    public static String getDay() {
      Calendar cdar = Calendar.getInstance();
      return getFormalYear(cdar) + getFormalMonth(cdar) + getFormalDay(cdar);    }

    /**
     * 서버의 현재일자 중 년도를 yyyy 형식의 스트링으로 리턴
     *
     * @return String yyyy
     */
    public static String getYear() {
      return getFormalYear(Calendar.getInstance());
    }

    /**
     * 서버의 현재일자 중 월을 mm 형식의 스트링으로 리턴
     *
     * @return String mm
     */
    public static String getMonth() {
      return getFormalMonth(Calendar.getInstance());
    }

    /**
     * 서버의 현재일자 중 일을 dd 형식의 스티링으로 리턴
     *
     * @return String dd
     */
    public static String getDayOfMonth() {
      return getFormalDay(Calendar.getInstance());
    }

    /**
     * 서버의 현재일자 중 요일을 한글명(1자리)으로 리턴
     *
     * @return String
     */
    public static String getDayOfWeek() {
      int i = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
      return koreanWeekStrings[i-1];
    }

    /**
     * 서버의 현재일자 중 요일을 숫자로 리턴(일요일 0)
     *
     * @return int
     */
    public static int getDayOfWeekOrdinal() {
      int i = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
      return (i-1);
    }
    
    /**
     * 입력받은 일자의 요일을 한글명(1자리)으로 리턴
     *
     * @param day String
     * @return String
     */
    public static String getDayOfWeek(String day) {
      int i = stringToCalendar(day).get(Calendar.DAY_OF_WEEK);
      return koreanWeekStrings[i-1];
    }

    /**
     * 입력받은 일자의 요일을 숫자로 리턴(일요일 0)
     *
     * @param day yyyymmdd
     * @return int
     */
    public static int getDayOfWeekOrdinal(String day) {
      int i = stringToCalendar(day).get(Calendar.DAY_OF_WEEK);
      return (i-1);
    }
    
    /**
     * 서버의 현재일시 중 시간을 HH 형식의 스트링으로 리턴
     *
     * @return String
     */
    public static String getHour() {
      return getFormalHour(Calendar.getInstance());
    }

    /**
     * 서버의 현재일시 중 분을 mm 형식의 스트링으로 리턴
     *
     * @return String
     */
    public static String getMinute() {
      return getFormalMin(Calendar.getInstance());
    }

    /**
     * 서버의 현재일시 중 초를 ss 형식의 스트링으로 리턴
     *
     * @return String
     */
    public static String getSecond() {
      return getFormalSec(Calendar.getInstance());
    }

    /**
     * yyyymmdd 형식의 날짜에 년을 더해서 리턴해준다.
     *
     * @param day String
     * @param addNum int
     * @return String
     */
    public static String addYears(String day, int addNum) {
      return add(day, Calendar.YEAR, addNum);
    }

    /**
     * yyyymmdd 형식의 날짜에 월을 더해서 리턴해준다.
     *
     * @param day String
     * @param addNum int
     * @return String
     */
    public static String addMonths(String day, int addNum) {
      return add(day, Calendar.MONTH, addNum);
    }

    /**
     * yyyymmdd 형식의 날짜에 일을 더해서 리턴해준다.
     *
     * @param day String
     * @param addNum int
     * @return String
     */
    public static String addDays(String day, int addNum) {
      return add(day, Calendar.DATE, addNum);
    }

    /**
     * 일자 간의 일수를 리턴<br>
     * 오늘부터 내일까지의 일수는 2가 리턴됨
     *
     * @param from String yyyymmdd
     * @param to String yyyymmdd
     * @return int
     */
    public static int getDays(String from, String to) {
      Calendar calFrom = stringToCalendar(from);
      Calendar calTo = stringToCalendar(to);
      long gab = calTo.getTimeInMillis() - calFrom.getTimeInMillis();
      long lDays = gab / (1000 * 60 * 60 * 24);
      return (int) lDays + 1;
    }

  /**
   * 일자 간의 일수를 원하는 형태로 리턴
   *
   * @param from String
   * @param to String
   * @param type int
   * <ul>
   * <li>FROM_DAY_ADD 한편넣기
   * <li>TO_DAY_ADD  한편넣기
   * <li>BOTH_DAY_ADD 양편넣기
   * <li>NONE_DAY_ADD 양편빼기
   * </ul>
   * @return int
   */
  public static int getDays(String from, String to, int type) {
    int days = 0;
    if (type == FROM_DAY_ADD) { //한편넣기
      days = getDays(from, to) - 1;
    } else if (type == TO_DAY_ADD) { //한편넣기
      days = getDays(from, to) - 1;
    } else if (type == BOTH_DAY_ADD) { //양편넣기
      days = getDays(from, to);
    } else if (type == NONE_DAY_ADD) { //양편빼기
      days = getDays(from, to) - 2;
    }
    return days;
  }

  /**
   * 유효한 일자인지 체크하여 true/false를 리턴
   *
   * @param date String
   * @return boolean
   */
  public static boolean isValidDate(String date) {
    boolean isValid = true;
    try {
      dateFormatCheck(date);
    } catch (ParseException e) {
      isValid = false;
    }
    return isValid;
  }

    /**
     * 일자간의 월수를 리턴
     *
     * @param from String yyyymmdd
     * @param to String yyyymmdd
     * @return int
     */
    public static int getMonths(String from, String to) {
      Calendar calFrom = stringToCalendar(from);
      Calendar calTo = stringToCalendar(to);
      int fromMonth = calFrom.get(Calendar.MONTH);
      int fromYear = calFrom.get(Calendar.YEAR);

      int toMonth = calTo.get(Calendar.MONTH);
      int toYear = calTo.get(Calendar.YEAR);

      int yearGab = (toYear - fromYear);
      int monthGab = toMonth - fromMonth;
      int gab = yearGab * 12 + monthGab;

      return gab;
    }

    /**
     * 일자간의 년수를 리턴
     *
     * @param from String yyyymmdd
     * @param strTo String yyyymmdd
     * @return int
     */
    public static int getYears(String from, String strTo) {
      Calendar calFrom = stringToCalendar(from);
      Calendar calTo = stringToCalendar(strTo);

      int fromYear = calFrom.get(Calendar.YEAR);
      int toYear = calTo.get(Calendar.YEAR);

      int gab = (toYear - fromYear);
      return gab;
    }

    /**
     * 일자간의 시간을 milli second 단위로 리턴
     *
     * @param from String yyyymmdd
     * @param to String yyyymmdd
     * @return long
     */
    public static long getTimesInMillis(String from, String to) {
      Calendar calFrom = stringToCalendar(from);
      Calendar calTo = stringToCalendar(to);
      return calTo.getTimeInMillis() - calFrom.getTimeInMillis();
    }

    /**
     * 문자열 타입의 일시(yyyymmdd)를 Date 타입으로 변환
     *
     * @param day String
     * @return Date
     */
    public static Date stringToDate(String day) {
      return stringToCalendar(day).getTime();
    }

    /**
     * <p>
     * 시간 형식의 문자열로 주어지는 시작과 끝 시간의 term을 계산.
     * </p>
     *
     * <pre>
     * String from   = "0700";
     * String to     = "1130";
     * String result = DateHelper.getTermTimeAsString(from, to);
     * </pre>
     * <code>result</code>는 <code>"0430"</code> 을 가지게 된다.
     *
     * @param from 시작 시간 (네자리)
     * @param to   끝 시간  (네자리)
     * @return 시작 시간에서 끝 시간을 뺀 시간 (네자리)
     * @see #getTimeCount(String, String)
     */
    public static String getTermTimeAsString(String from, String to) throws ParseException {

        String currentDateStr = getCurrentDateAsString();

        if (StringHelper.null2void(from).length() != 4) {
            throw new ParseException("from hour must be 'hhmm' format.",0);
        }

        if (StringHelper.null2void(to).length() != 4) {
            throw new ParseException("to hour must be 'hhmm' format.",0);
        }

        from = currentDateStr.concat(from);
        to = currentDateStr.concat(to);

        long duration = getTimeCount(from, to);

        int hour = (int) (duration/(1000*60*60)); // 시간 계산
        int minute = (int) (duration%(1000*60*60))/(1000*60); // 분 계산

        return StringHelper.lPad(Integer.toString(hour), 2, '0')
        		.concat(StringHelper.lPad(Integer.toString(minute), 2, '0'));
    }

    /**
     * <p>
     * 시간 형식의 문자열로 받은 시작 시간에서 끝시간을 더한 시간 리턴.
     * </p>
     *
     * <pre>
     * String from = "0700";
     * String to = "0100";
     * String result = DateHelper.getCalcTimeAsString(from, to);
     * </pre>
     * <code>result</code>는 <code>"0800"</code> 을 가지게 된다.
     *
     * @param from 시작 시간 (네자리)
     * @param to   끝 시간  (네자리)
     * @return 시작 시간에서 끝 시간을 더한 시간 (네자리)
     * @throws ParseException
     * @see #getTimeCount(String, String)
     */
    public static String getCalcTimeAsString(String from, String to) throws ParseException {

        if (StringHelper.null2void(from).length() != 4) {
            throw new ParseException("from hour must be 'hhmm' format.",0);
        }

        if (StringHelper.null2void(to).length() != 4) {
            throw new ParseException("to hour must be 'hhmm' format.",0);
        }

        int fromMinute = Integer.parseInt(hourToMinute(from));
        int toMinute = Integer.parseInt(hourToMinute(to));

        int sumMinute = fromMinute + toMinute;

        return minuteToHour(String.valueOf(sumMinute));
    }

    /**
     * <p>
     * 분을 시간 형식으로 변경.
     * </p>
     *
     * <pre>
     * String min = "150";
     * String result = DateHelper.getMin2Tim(min);
     * </pre>
     * <code>result</code>는 <code>"0230"</code> 을 가지게 된다.
     *
     * @param minute 분
     * @return 시간분(네자리)
     */
    public static String minuteToHour(String minute) {
        long duration = Long.parseLong(minute);

        int hour = (int) (duration/60);
        int min = (int) (duration%60);

        return StringHelper.lPad(Integer.toString(hour), 2, '0')
        		.concat(StringHelper.lPad(Integer.toString(min), 2, '0'));
    }

    /**
     * <p>
     * 시간 형식을 분 형식으로 변경.
     * </p>
     *
     * <pre>
     * String min = "0230";
     * String result = DateHelperExt.getTim2Min(min);
     * </pre>
     * <code>result</code>는 <code>"150"</code> 을 가지게 된다.
     *
     * @param time 분
     * @return 시간분(네자리)
     */
    public static String hourToMinute(String time) throws ParseException {

        if (StringHelper.null2void(time).length() != 4) {
            throw new ParseException("time must be 'hhmm' format.",0);
        }

        int hour = Integer.parseInt(StringHelper.toSubString(time, 0, 2));
        int min = Integer.parseInt(StringHelper.toSubString(time, 2, 4));

        // 시를 분으로 계산
        hour = (hour * 60);

        // 분으로 계산한 시를 분과 합
        min = hour + min;

        return String.valueOf(min);
    }

    /**
     * <p>
     * 해당 월의 달력 데이터를 생성하여 ValueObject로 제공하는 함수.
     * </p>
     *
     * @param sDate "yyyyMMdd" format으로 넘겨주어야 함. dd는 사용하지 않음
     * @param korean : 요일 컬럼을 한글로 할지 여부
     * @return : ValueObject 해당월의 달력
     * @throws ParseException
     */
    public static ValueObject getCalendarInfo(String sDate, boolean korean) throws ParseException {
        ValueObject resultVO = new ValueObject();
        String[] weekname = null;
        if (korean) {
        	weekname = koreanWeekStrings;
        } else {
        	weekname = englishWeekStrings;
        }
        
        int yyyy = 0;
        int mm = 0;

        if (StringHelper.isNull(sDate)) {
            yyyy = getCurrentYearAsInt();
            mm = getCurrentMonthAsInt();
        } else {
            dateFormatCheck(sDate);

            yyyy = Integer.parseInt(sDate.substring(0, 4));
            mm = Integer.parseInt(sDate.substring(4, 6));
        }
        int startIdx = getDayOfWeek(yyyy, mm, 1); //그 달의 첫번재 일의 day of week값을 구한다.
        int endIdx = getDayCountForMonth(yyyy, mm);  //그 달의 마지막 날을 구한다.

        //setting
        for (int i = 0, day = 0; i < 42; i++) {  //42 = 달력에서 최대로 나올수 있는 칸수 (7 * 6)
            if (i >= (startIdx - 1) && i < (startIdx + endIdx - 1)) {
                day++;
                resultVO.set(i / 7, weekname[i % 7], day);
            } else {
                resultVO.set(i / 7, weekname[i % 7], "");
            }
        }

        return resultVO;
    }

    /**
     * <p>
     * 해당 월의 달력 데이터를 생성하여 ValueObject로 제공하는 함수.
     * 요일 컬럼은 영문으로 넣어준다.
     * </p>
     *
     * <pre> 제공자 : 조환석 과장님 </pre>
     *
     * @param sDate "yyyyMMdd" format으로 넘겨주어야 함. dd는 사용하지 않음
     * @return : ValueObject 해당월의 달력
     * @throws ParseException
     */
    public static ValueObject getCalendarInfo(String sDate) throws ParseException {
    	return getCalendarInfo(sDate,false);
    }
    
    /**
     * <p>
     * 해당월의 날짜를 주에 몇번째 요일인지 int형으로 리턴.<br>
     * [sun, mon, thu, wed, thu, fri, sat 순이며 좌측부터 index 1부터 시작한다.]
     * </p>
     *
     * @param yyyy 년도
     * @param mm   월
     * @param dd   일
     * @return 해당 주에 몇번째 일자
     */
    public static int getDayOfWeek(int yyyy, int mm, int dd) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(yyyy, mm - 1, dd);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
    
    /**
     * 문자열 타입의 일시(yyyymmdd)를 Calendar 타입으로 변환
     *
     * @param day String
     * @return Calendar
     */
    private static Calendar stringToCalendar(String day) {
      String strYear = "";
      String strMonth = "";
      String strDate = "";

      strYear = day.substring(0, 4);
      strMonth = day.substring(4, 6);
      strDate = day.substring(6);

      int iYear = Integer.parseInt(strYear);
      int iMonth = Integer.parseInt(strMonth) - 1;
      int iDate = Integer.parseInt(strDate);

      Calendar cz_Tmp = Calendar.getInstance();
      cz_Tmp.set(iYear, iMonth, iDate);
      return cz_Tmp;
    }

    /**
     * yyyymmdd타입의 날짜에 년, 월 혹은 일을 더해서 리턴해준다.
     *
     * @param day 일자
     * @param i Calendar.YEAR, alendar.MONTH, Calendar.DATE
     * @param addNum 더할 숫자
     * @return String
     */
    private static String add(String day, int i, int addNum) {
      Date d = stringToDate(day);
      Calendar cdar = Calendar.getInstance();
      cdar.clear();
      cdar.setTime(d);
      cdar.add(i, addNum);
      return getFormalYear(cdar) + getFormalMonth(cdar) + getFormalDay(cdar);
    }    
    
    /**
     * 지정된 포맷으로 date를 String으로 리턴한다. 
     * 
	 * @return
	 */
	public static String getSimpleDate(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(new Date());
	}
	
	/**
	 * <p>
	 * 월의 마지막 일자 구하기
	 * <p>
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDay(String year, String month) {
		Integer[] lastdate = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }; // 각 달의 마지막 날짜

		int yearInt = Integer.parseInt(year);
		int monthInt = Integer.parseInt(month);

		if ((0 == (yearInt % 4) && 0 != (yearInt % 100)) || 0 == (yearInt % 400)) { // year를 가지고 윤년인지 검사.
			lastdate[1] = 29; // 윤년인 경우 2월의 마지막 날짜를 29로 입력
		}

		Integer day = lastdate[monthInt - 1];

		return day.toString();
	}
}
