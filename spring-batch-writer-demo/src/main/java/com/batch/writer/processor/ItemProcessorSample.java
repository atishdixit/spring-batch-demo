package com.batch.writer.processor;

import com.batch.writer.model.User;
import com.batch.writer.model.UserDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ItemProcessorSample implements ItemProcessor<User, UserDto> {

    @Override
    public UserDto process(User user) throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        return userDto;
    }
}
