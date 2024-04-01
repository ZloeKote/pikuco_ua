package com.pikuco.userservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateRangeValidator implements ConstraintValidator<DateRange, Date> {
    private String minDate;
    private String maxDate;

    @Override
    public void initialize(DateRange dateRange) {
        minDate = dateRange.minDate();
        maxDate = dateRange.maxDate();
    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return true;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = sdf.parse(minDate);
            Date endDate = sdf.parse(maxDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Date actualDate = calendar.getTime();

            return actualDate.after(startDate) && actualDate.before(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
