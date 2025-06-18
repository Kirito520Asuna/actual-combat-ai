package com.actual.combat.gen.domain;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.basic.abs.service.AbsValidate;
import com.actual.combat.basic.utils.str.StrUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Schema(description = "生成插入祖先表sql")
@SuperBuilder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class BuildAncestorSql implements Serializable, AbsValidate<BuildAncestorSql> {
    @Schema(description = "基础表名")
    String tableName;
    @Schema(description = "基础表id名")
    String tableId;
    @Schema(description = "基础表父id名")
    String tableParentId;
    @Schema(description = "基础表层级名")
    String tableLevel;

    @Schema(description = "祖先表名")
    String tableAncestorName;
    @Schema(description = "祖先表id名")
    String tableAncestorId;
    @Schema(description = "祖先表父id名")
    String tableAncestorParentId;
    @Schema(description = "祖先表层级名")
    String tableAncestorLevel;
    @Schema(description = "idsStr: 1,2,3")
    String idsStr;
    @Schema(description = "id集合")
    List<String> ids;

    @Override
    public boolean validateOk(BuildAncestorSql buildAncestorSql) {
        if (StrUtils.isBlank(buildAncestorSql.tableName) || StrUtils.isBlank(buildAncestorSql.tableId) || StrUtils.isBlank(buildAncestorSql.tableParentId) || StrUtils.isBlank(buildAncestorSql.tableLevel) || StrUtils.isBlank(buildAncestorSql.tableAncestorName) || StrUtils.isBlank(buildAncestorSql.tableAncestorId) || StrUtils.isBlank(buildAncestorSql.tableAncestorParentId) || StrUtils.isBlank(buildAncestorSql.tableAncestorLevel)) {
            return false;
        }

        if (StrUtils.isNotBlank(buildAncestorSql.idsStr)) {
            buildAncestorSql.ids = CollUtil.isEmpty(buildAncestorSql.ids) ? CollUtil.newArrayList() : buildAncestorSql.ids;
            buildAncestorSql.ids.addAll(
                    StrUtils.split(buildAncestorSql.idsStr, ",").stream().map(String::trim).collect(Collectors.toList())
            );
        }

        if (CollUtil.isEmpty(buildAncestorSql.ids)) {
            return false;
        }
        return true;
    }

    public String buildSql() {
        return buildSql(this);
    }

    private String buildSql(BuildAncestorSql buildAncestorSql) {
        return buildSql(buildAncestorSql.tableName, buildAncestorSql.tableId, buildAncestorSql.tableParentId, buildAncestorSql.tableLevel, buildAncestorSql.tableAncestorName, buildAncestorSql.tableAncestorId, buildAncestorSql.tableAncestorParentId, buildAncestorSql.tableAncestorLevel, buildAncestorSql.ids);
    }

    private String buildSql(String tableName, String tableId, String tableParentId, String tableLevel, String tableAncestorName, String tableAncestorId, String tableAncestorParentId, String tableAncestorLevel, List<String> list) {
        if (StrUtils.isBlank(tableName)) {
            tableName = "tableName";
        }
        if (StrUtils.isBlank(tableId)) {
            tableId = "tableId";
        }

        if (StrUtils.isBlank(tableParentId)) {
            tableParentId = "tableParentId";
        }

        if (StrUtils.isBlank(tableLevel)) {
            tableLevel = "tableLevel";
        }

        if (StrUtils.isBlank(tableAncestorName)) {
            tableAncestorName = "tableAncestorName";
        }

        if (StrUtils.isBlank(tableAncestorId)) {
            tableAncestorId = "tableAncestorId";
        }

        if (StrUtils.isBlank(tableAncestorParentId)) {
            tableAncestorParentId = "tableAncestorParentId";
        }
        if (StrUtils.isBlank(tableAncestorLevel)) {
            tableAncestorLevel = "tableAncestorLevel";
        }

        if (CollUtil.isEmpty(list)) {
            throw new RuntimeException("ids不能为空");
        }

        String startInsert = "INSERT INTO `" + tableAncestorName + "` (`" + tableAncestorId + "`,`" + tableAncestorParentId + "`, `" + tableAncestorLevel + "`) ";
        String start = "    SELECT `" + tableId + "`, `" + tableParentId + "`, `" + tableLevel + "` " +
                "   FROM (";
        String end = "  ) AS subquery;  ";

        String unionAll = " UNION ALL ";
        //String id = "1";

        int index = 0;
        StringBuilder builder = new StringBuilder(startInsert).append(start);
        for (String id : list) {
            if (index != 0) {
                builder.append(unionAll);
            }
            String table = replaceTable(tableName, tableId, tableParentId, tableLevel, id);
            builder.append(table);
            index++;
        }
        builder.append(end);
        String sql = builder.toString();
        return sql;
    }

    private String replaceTable(String tableName, String tableId, String tableParentId, String tableLevel, String id) {
        String ft =
                "    SELECT @Id as `tableId`, @Id as `tableParentId`, 0 as `tableLevel`   " +
                        "    UNION ALL  " +
                        "    (WITH RECURSIVE ParentMenus AS (   " +
                        "    SELECT `tableId`, `tableParentId`, 1 AS `tableLevel` " +
                        "    FROM `tableName` " +
                        "    WHERE `tableId` = @Id    " +
                        "    UNION ALL  " +
                        "    SELECT m.`tableId`, m.`tableParentId`, pm.`tableLevel` + 1   " +
                        "    FROM `tableName` m   " +
                        "    INNER JOIN ParentMenus pm ON m.`tableId` = pm.`tableParentId`  " +
                        "    )  " +
                        "    SELECT @Id as `tableId`, `tableParentId`, `tableLevel`   " +
                        "    FROM ParentMenus   " +
                        "    WHERE `tableParentId` != 0)  ";
        String table = ft.replace("tableName", tableName)
                .replace("tableId", tableId)
                .replace("tableParentId", tableParentId)
                .replace("tableLevel", tableLevel)
                .replace("@Id", id);
        return table;
    }
}