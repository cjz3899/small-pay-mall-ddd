package com.junzhecai.api;

import com.junzhecai.api.dto.CreatePayRequestDTO;
import com.junzhecai.api.response.Response;

public interface IPayService {

    Response<String> createPayOrder(CreatePayRequestDTO createPayRequestDTO);

    
}
