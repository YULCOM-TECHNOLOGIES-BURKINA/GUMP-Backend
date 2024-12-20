package com.yulcomtechnologies.tresorms.events;

import com.yulcomtechnologies.sharedlibrary.events.Dispatchable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DocumentRequestChanged implements Dispatchable {
    private Long documentRequestId;
}
