package com.batch.xml.writer;

import java.util.List;

import com.batch.xml.model.User;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class XMLItemWriter implements ItemWriter<User> {

	@Override
	public void write(List<? extends User> items) throws Exception {
		System.out.println("Inside Item Writer");
		items.stream().forEach(System.out::println);
	}

}
