package com.actual.combat.gen.controller;

import com.actual.combat.aop.all.log.SysLog;
import com.actual.combat.basic.core.template.BasicController;
import com.actual.combat.basic.result.Result;
import com.actual.combat.gen.domain.BuildAncestorSql;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "sql ", description = "sql")
@RestController
@RequestMapping({"/api/build/sql/", "/jwt/build/sql/", "/build/sql/"})
public class SqlController implements BasicController {

    @SysLog
    @Operation(summary = "生成祖先表插入sql")
    @PostMapping("generate/insert/ancestor")
    public Result<String> generateAncestor(@RequestBody BuildAncestorSql buildAncestorSql) {
        buildAncestorSql.validateOk();
        return ok(buildAncestorSql.buildSql());
    }

}
