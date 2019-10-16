package com.revolut.pojo;

import java.io.Serializable;

public interface ResponseMessage extends Serializable {
    long serialVersionUID = 1L;
    String NO_RECORD = "No Data Found";
    String INTERNAL_ERR = "Internal Error";
}