package com.imooc.passbook.service.impl;

import com.imooc.passbook.Util.RowKeyGenUtil;
import com.imooc.passbook.constant.Constants;
import com.imooc.passbook.service.IHBasePassService;
import com.imooc.passbook.vo.PassTemplate;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Pass HBase 服务实现
 */
@Slf4j
@Service
public class HBasePassServiceImpl implements IHBasePassService {

    /**
     * HBase客户端
     */
    private final HbaseTemplate hbaseTemplate;

    @Autowired
    public HBasePassServiceImpl(HbaseTemplate hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    @Override
    public boolean dropPassTemplateToHBase(PassTemplate passTemplate) {

        //1. 检查传入的VO对象是否为空
        if(null == passTemplate) {
            return false;
        }

        //2. 根据传入的VO对象，调用rowKey生成器，生成rowKey
        String rowKey = RowKeyGenUtil.genPassTemplateRowKey(passTemplate);

        //3. 检查当前rowKey是否已经在HBase中存在
        try {
            if(hbaseTemplate
                    .getConnection()
                    .getTable(TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME))
                    .exists(new Get(Bytes.toBytes(rowKey)))) {
                log.warn("RowKey {} is already exist!", rowKey);
            }
        } catch(Exception e) {
            log.error("DropPassTemplateToHBase Error: {}", e);
            return false;
        }

        //4. 存储当前传入的VO对象

        //4.1. 根据rowKey生成put对象
        Put put = new Put(Bytes.toBytes(rowKey));

        //4.2. 将VO对象中各个字段数据，匹配到对应的列族和列中，统一添加进put对象中，进行收集
        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.ID),
                Bytes.toBytes(passTemplate.getId())
        );

        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.TITLE),
                Bytes.toBytes(passTemplate.getTitle())
        );

        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.SUMMARY),
                Bytes.toBytes(passTemplate.getSummary())
        );

        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.DESC),
                Bytes.toBytes(passTemplate.getDesc())
        );

        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.HAS_TOKEN),
                Bytes.toBytes(passTemplate.getHasToken())
        );

        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B),
                Bytes.toBytes(Constants.PassTemplateTable.BACKGROUND),
                Bytes.toBytes(passTemplate.getBackground())
        );

        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                Bytes.toBytes(passTemplate.getLimit())
        );

        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                Bytes.toBytes(Constants.PassTemplateTable.START),
                Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(passTemplate.getStart()))
        );

        put.addColumn(
                Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                Bytes.toBytes(Constants.PassTemplateTable.END),
                Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(passTemplate.getEnd()))
        );

        //4.3. 构建好put对象后，存储进HBase中
        hbaseTemplate.saveOrUpdate(Constants.PassTemplateTable.TABLE_NAME, put);

        return true;
    }
}
