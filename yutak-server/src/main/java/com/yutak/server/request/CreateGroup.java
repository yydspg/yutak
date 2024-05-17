package com.yutak.server.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateGroup {
    private String groupName;
    private String uids;
}
