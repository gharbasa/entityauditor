package com.dt.rts.eregusa.audit.config;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.amazonaws.services.kinesis.model.PutRecordsResultEntry;

@Service(value = "KinesisPutDataStream")
public class KinesisPutDataStream {
	Logger logger = LoggerFactory.getLogger(KinesisPutDataStream.class);
	
	//AmazonKinesisClientBuilder clientBuilder;
	AmazonKinesis kinesisClient;
	
	public KinesisPutDataStream() {
		//AmazonKinesisClientBuilder clientBuilder = AmazonKinesisClientBuilder.standard();
        //clientBuilder.setRegion(regionName);
        //clientBuilder.setCredentials(credentialsProvider);
        //clientBuilder.setClientConfiguration(config);
		//AmazonKinesis kinesisClient = clientBuilder.build();
	}
	
	@PostConstruct
	public void PostConstruct() {
		logger.info("KinesisPutDataStream PostConstruct");
		AmazonKinesisClientBuilder clientBuilder = AmazonKinesisClientBuilder.standard();
		kinesisClient = clientBuilder.build();
		logger.info("After KinesisPutDataStream PostConstruct");
	}
	
	public void putData(byte[] data, InterceptorConfig interceptorConfig, String partitionKey) {
		logger.info("KinesisPutDataStream putData interceptorConfig.getKinesisDatastream()=" + interceptorConfig.getKinesisDatastream());
		PutRecordsRequest putRecordsRequest  = new PutRecordsRequest();
        putRecordsRequest.setStreamName(interceptorConfig.getKinesisDatastream());
        List <PutRecordsRequestEntry> putRecordsRequestEntryList  = new ArrayList<>(); 
        //for (int i = 0; i < 100; i++) {
            PutRecordsRequestEntry putRecordsRequestEntry  = new PutRecordsRequestEntry();
            putRecordsRequestEntry.setData(ByteBuffer.wrap(data));
            putRecordsRequestEntry.setPartitionKey(String.format("partitionKey-%s", partitionKey));
            putRecordsRequestEntryList.add(putRecordsRequestEntry); 
        //}
        
        putRecordsRequest.setRecords(putRecordsRequestEntryList);
        PutRecordsResult putRecordsResult  = kinesisClient.putRecords(putRecordsRequest);
        logger.info("Put Result" + putRecordsResult + ", putRecordsResult.getFailedRecordCount()=" + putRecordsResult.getFailedRecordCount());
        
        while (putRecordsResult.getFailedRecordCount() > 0) {
            final List<PutRecordsRequestEntry> failedRecordsList = new ArrayList<>();
            final List<PutRecordsResultEntry> putRecordsResultEntryList = putRecordsResult.getRecords();
            for (int i = 0; i < putRecordsResultEntryList.size(); i++) {
                final PutRecordsRequestEntry putRecordRequestEntry = putRecordsRequestEntryList.get(i);
                final PutRecordsResultEntry putRecordsResultEntry = putRecordsResultEntryList.get(i);
                if (putRecordsResultEntry.getErrorCode() != null) {
                    failedRecordsList.add(putRecordRequestEntry);
                }
            }
            putRecordsRequestEntryList = failedRecordsList;
            putRecordsRequest.setRecords(putRecordsRequestEntryList);
            putRecordsResult = kinesisClient.putRecords(putRecordsRequest);
        }
        
        logger.info("Put Result complete...");
        
	}
}
