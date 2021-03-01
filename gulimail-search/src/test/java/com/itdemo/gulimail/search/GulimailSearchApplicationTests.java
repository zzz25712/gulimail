package com.itdemo.gulimail.search;


import com.alibaba.fastjson.JSON;
import com.itdemo.gulimail.search.config.ElasticConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimailSearchApplicationTests {

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Test
	public void contextLoads() {
		System.out.println(restHighLevelClient);
	}
   	@Test
   	public void searchData() throws IOException {
		SearchRequest request = new SearchRequest();//创建查询请求


		//构建查询条件
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

		//构建聚合条件
		TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
		searchSourceBuilder.aggregation(ageAgg);
		AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
		searchSourceBuilder.aggregation(balanceAvg);
//		System.out.println("条件："+searchSourceBuilder);

		request.source(searchSourceBuilder);//传入查询条件

		SearchResponse searchResponse = restHighLevelClient.search(request, ElasticConfig.COMMON_OPTIONS);
//		System.out.println("结果："+searchResponse);

		//分析查询结果
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit searchHit : searchHits) {
			String string = searchHit.getSourceAsString();
			Account account = JSON.parseObject(string, Account.class);
			System.out.println("account:"+account);
		}
		System.out.println("--------");

		//分析聚合结果
		Aggregations aggregations = searchResponse.getAggregations();
		Terms ageAgg1 = aggregations.get("ageAgg");
		ageAgg1.getBuckets().forEach(bucket->{
			System.out.println("年龄："+bucket.getKeyAsString()+"renshu:"+bucket.getDocCount());
		});
	}

    @Test
	public void saveEs() throws IOException {
		IndexRequest request = new IndexRequest("user"); //创建user索引的请求
		request.id("1"); //设置id
		User user = new User();
		user.setAge(10);
		user.setName("haha");
		String jsonString = JSON.toJSONString(user);
		request.source(jsonString, XContentType.JSON);

		//执行操作
		IndexResponse index = restHighLevelClient.index(request, ElasticConfig.COMMON_OPTIONS);
		System.out.println(index);
	}

	@Data
	class User{
		Integer age;
		String name;
	}


	@ToString
	@Data
	static class Account {

		private int account_number;
		private int balance;
		private String firstname;
		private String lastname;
		private int age;
		private String gender;
		private String address;
		private String employer;
		private String email;
		private String city;
		private String state;
	}

}
