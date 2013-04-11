package com.profiler.server.dao.hbase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.profiler.common.ServiceType;
import com.profiler.common.dto2.thrift.Annotation;
import com.profiler.common.dto2.thrift.Span;
import com.profiler.common.hbase.HBaseAdminTemplate;
import com.profiler.common.hbase.HBaseTables;
import com.profiler.common.hbase.HbaseOperations2;
import com.profiler.common.util.RowKeyUtils;
import com.profiler.common.util.SpanUtils;

@RunWith(SpringJUnit4ClassRunner.class)
//@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@ContextConfiguration("classpath:test-applicationContext.xml")
public class TraceDaoTest {

    private static final String TRACE = "Trace";
    private static final String ID = "ID";

    @Autowired
    private HbaseOperations2 hbaseOperations;

    @Autowired
    private HBaseAdminTemplate hBaseAdminTemplate;


    @Autowired
    @Qualifier("testTraceIndex")
    private HbaseTraceIndexDao traceIndex;


    //	@BeforeClass
    @Before
    public void init() throws IOException {
        if (hBaseAdminTemplate == null) {
            System.out.println("hBaseAdmin is null-------");
            return;
        }
        String tableName = traceIndex.getTableName();

        HTableDescriptor testTrace = new HTableDescriptor(traceIndex.getTableName());
        testTrace.addFamily(new HColumnDescriptor(TRACE));
        hBaseAdminTemplate.createTableIfNotExist(testTrace);

    }

    //	@AfterClass
    @After
    public void destroy() throws IOException {
        String tableName = traceIndex.getTableName();
        hBaseAdminTemplate.dropTableIfExist(tableName);

    }

    RowMapper<byte[]> valueRowMapper = new RowMapper<byte[]>() {
        @Override
        public byte[] mapRow(Result result, int rowNum) throws Exception {
            return result.value();
        }
    };

    @Test
    public void insertSpan() throws InterruptedException, UnsupportedEncodingException {
        final Span span = createSpan();

        traceIndex.insert(span);
        // TODO 서버가 받은 시간으로 변경해야 될듯.
        byte[] rowKey = RowKeyUtils.concatFixedByteAndLong(Bytes.toBytes(span.getAgentId()), HBaseTables.AGENT_NAME_MAX_LEN, span.getStartTime());
        byte[] result = hbaseOperations.get(traceIndex.getTableName(), rowKey, Bytes.toBytes("Trace"), Bytes.toBytes("ID"), valueRowMapper);

        Assert.assertArrayEquals(SpanUtils.getTraceId(span), result);
    }

    private Span createSpan() {
        UUID uuid = UUID.randomUUID();
        List<Annotation> ano = Collections.emptyList();
        long l = System.currentTimeMillis();
        
        Span span = new Span();
        
        span.setAgentId("UnitTest");
        span.setApplicationId("testApplication");
        span.setMostTraceId(uuid.getMostSignificantBits());
        span.setLeastTraceId(uuid.getLeastSignificantBits());
        span.setStartTime(l);
        span.setElapsed(5);
        span.setRpc("RPC");
        span.setServiceType(ServiceType.UNKNOWN.getCode());
        span.setAnnotations(ano);
        
        return span;
    }
}
