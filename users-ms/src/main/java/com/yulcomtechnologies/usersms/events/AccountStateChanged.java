package com.yulcomtechnologies.usersms.events;

import com.yulcomtechnologies.sharedlibrary.events.Dispatchable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class AccountStateChanged implements Dispatchable {
    public Long userId;
}
