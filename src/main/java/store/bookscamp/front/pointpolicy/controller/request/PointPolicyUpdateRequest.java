package store.bookscamp.front.pointpolicy.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import store.bookscamp.front.pointpolicy.controller.enums.PointPolicyType;
import store.bookscamp.front.pointpolicy.controller.enums.RewardType;

public record PointPolicyUpdateRequest(

        @NotNull
        PointPolicyType pointPolicyType,

        @NotNull
        RewardType rewardType,

        @NotNull
        @Positive
        Integer rewardValue
) {
}
