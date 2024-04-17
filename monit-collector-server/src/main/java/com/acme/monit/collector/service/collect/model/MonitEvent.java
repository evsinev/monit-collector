package com.acme.monit.collector.service.collect.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitEvent {

    private static final String[] ACTION_NAMES = {"ignore", "alert", "restart", "stop", "exec", "unmonitor", "start", "monitor", ""};

    String service;
    String message;
    int    type;
    int    state;
    int    action;

    public MonitEventActionType getActionType() {
        switch (action) {
            case 0: return MonitEventActionType.IGNORE;
            case 1: return MonitEventActionType.ALERT;
            case 2: return MonitEventActionType.RESTART;
            case 3: return MonitEventActionType.STOP;
            case 4: return MonitEventActionType.EXEC;
            case 5: return MonitEventActionType.UNMONITOR;
            case 6: return MonitEventActionType.START;
            case 7: return MonitEventActionType.MONITOR;
            default:
                return MonitEventActionType.UNKNOWN;
        }
    }

    public MonitEventStateType getStateType() {
        for (MonitEventStateType value : MonitEventStateType.values()) {
            if(state == value.getCode()) {
                return value;
            }
        }

        return MonitEventStateType.UNKNOWN;
    }

    public MonitEventServiceType getType() {
        for (MonitEventServiceType serviceType : MonitEventServiceType.values()) {
            if(serviceType.getCode() == type) {
                return serviceType;
            }
        }

        return MonitEventServiceType.UNKNOWN;
    }

}
