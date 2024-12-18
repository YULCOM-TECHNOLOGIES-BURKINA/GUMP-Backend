package com.yulcomtechnologies.drtssms.events;

import com.yulcomtechnologies.sharedlibrary.events.Dispatchable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class NewDocumentRequest implements Dispatchable {
 private String region;

}