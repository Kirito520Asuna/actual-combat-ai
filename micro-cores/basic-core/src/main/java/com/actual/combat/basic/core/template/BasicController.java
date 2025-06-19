package com.actual.combat.basic.core.template;

import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.page.AbsPage;
import com.actual.combat.basic.page.ResultPage;
import com.actual.combat.basic.result.Result;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/29 下午5:59:24
 * @Description
 */
@RestController
public interface BasicController extends AbsBean {
    default AbsPage fetchAbsPage() {
        return new ResultPage();
    }
    default <T> Result<T> ok() {
        return Result.ok();
    }

    default <T> Result<T> ok(T data) {
        return Result.ok(data);
    }

    default <T> Result<T> fail() {
        return Result.fail();
    }

    default <T> Result<T> fail(String message) {
        return Result.fail(message);
    }

    default <T> Result<ResultPage<T>> listToPage(List<T> list) {
        return ok(fetchAbsPage().listToPage(list));
    }
}
