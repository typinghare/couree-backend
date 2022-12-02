package com.couree.luckycat.app.responder;

import com.couree.luckycat.app.error.stereo.RequestException;
import com.couree.luckycat.glacier.annotation.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author James Chan
 */
@Controller
@RequestMapping()
public class DefaultController {
    /**
     * This controller method matches any unmatched cases. Backup means 备胎. 这tm不是备胎是什么？😅
     * 有人问我：我去，有没有搞错啊，写个程序都能看见备胎？我的回答是：一日当备胎，生活处处是备胎。😅
     */
    @RequestMapping("/**")
    public String backup() {
        throw RequestException.NO_MATCHING_HANDLER_METHOD;
    }
}
