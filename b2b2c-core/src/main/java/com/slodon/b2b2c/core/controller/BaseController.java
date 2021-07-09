package com.slodon.b2b2c.core.controller;

import com.slodon.b2b2c.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 控制层基类， 所有controller类都需要继承此类
 */
@Slf4j
public class BaseController {

    protected boolean isNull(Object... argumets) {
        if (argumets == null) {
            return true;
        }
        for (Object obj : argumets) {
            if (obj == null || "".equals(obj)) {
                return true;
            }
        }
        return false;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        //date,datetime
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                if (StringUtil.isEmpty(value)) {
                    setValue(null);
                    return;
                }
                try {
                    if (value.length() == 10) {
                        setValue(new SimpleDateFormat("yyyy-MM-dd").parse(value));
                    } else if (value.length() == 8) {
                        setValue(new SimpleDateFormat("HH:mm:ss").parse(value));
                    } else if (value.length() == 16) {
                        setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(value));
                    } else {
                        setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value));
                    }

                } catch (ParseException e) {
                    log.error("Can not convert '" + value + "' to java.util.Date", e);
                }
            }

            @Override
            public String getAsText() {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) getValue());
            }

        });
        //int
        binder.registerCustomEditor(Integer.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                if (StringUtil.isEmpty(value)) {
                    setValue(0);
                    return;
                }
                setValue(Integer.parseInt(value));
            }

            @Override
            public String getAsText() {
                return getValue().toString();
            }

        });

        //long
        binder.registerCustomEditor(Long.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                if (StringUtil.isEmpty(value)) {
                    setValue(0);
                    return;
                }
                setValue(Long.parseLong(value));
            }

            @Override
            public String getAsText() {
                return getValue().toString();
            }

        });

        //double
        binder.registerCustomEditor(Double.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                if (StringUtil.isEmpty(value)) {
                    setValue(0);
                    return;
                }
                setValue(Double.parseDouble(value));
            }

            @Override
            public String getAsText() {
                return getValue().toString();
            }

        });
    }

}
