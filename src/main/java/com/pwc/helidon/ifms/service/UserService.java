package com.pwc.helidon.ifms.service;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.pwc.helidon.ifms.commons.IFMSException;
import com.pwc.helidon.ifms.model.UserDetails;
import com.pwc.helidon.ifms.repository.UserRepository;

@Dependent
public class UserService {

	private final UserRepository userRepository;

	@Inject
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserDetails getUserDetail(String ssoid) throws IFMSException {
		return userRepository.getUserDetailsBySSOID(ssoid);
	}

}
