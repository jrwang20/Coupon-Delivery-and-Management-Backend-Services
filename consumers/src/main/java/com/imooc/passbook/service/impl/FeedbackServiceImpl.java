package com.imooc.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.passbook.Util.RowKeyGenUtil;
import com.imooc.passbook.constant.Constants;
import com.imooc.passbook.mapper.FeedbackRowMapper;
import com.imooc.passbook.service.IFeedbackService;
import com.imooc.passbook.vo.FeedBack;
import com.imooc.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论功能服务实现
 */
@Slf4j
@Service
public class FeedbackServiceImpl implements IFeedbackService {

    /** HBase客户端 */
    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Override
    public Response createFeedback(FeedBack feedBack) {

        //1. 如果feedback不通过验证，那么直接返回错误信息
        if(!feedBack.validate()) {
            log.error("Feedback Error: {}", JSON.toJSONString(feedBack));
            return Response.error("Feedback Error");
        }

        //2. 创建put对象
        Put put = new Put(Bytes.toBytes(RowKeyGenUtil.genFeedbackRowKey(feedBack)));

        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.USER_ID),
                Bytes.toBytes(feedBack.getUserId())
        );

        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.TYPE),
                Bytes.toBytes(feedBack.getType())
        );

        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.TEMPLATE_ID),
                Bytes.toBytes(feedBack.getTemplateId())
        );

        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.COMMENT),
                Bytes.toBytes(feedBack.getComment())
        );

        //3. hbase客户端保存
        hbaseTemplate.saveOrUpdate(Constants.Feedback.TABLE_NAME, put);

        return Response.success();
    }

    @Override
    public Response getFeedback(Long userId) {

        //1. 将userId反转过来，在生成byte数组(这是因为feedback的rowKey生成器是要先反转，所以这里重新反转)
        byte[] reverseUserId = new StringBuilder(String.valueOf(userId))
                                    .reverse()
                                    .toString()
                                    .getBytes();

        //2. 创建scan对象，并设置前缀过滤器，过滤出和给定数据又同样前缀的数据
        Scan scan = new Scan();
        scan.setFilter(new PrefixFilter(reverseUserId));

        //3. 利用hbase客户端，根据Scan确定的扫描范围、目标的HBase表名、ORM数据转化，进行数据查询
        List<FeedBack> feedBackList = hbaseTemplate.find(Constants.Feedback.TABLE_NAME, scan, new FeedbackRowMapper());

        return new Response(feedBackList);
    }
}
