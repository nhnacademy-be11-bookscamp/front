package store.bookscamp.front.pointpolicy.controller.response;

import store.bookscamp.front.pointpolicy.controller.enums.PointPolicyType;
import store.bookscamp.front.pointpolicy.controller.enums.RewardType;

public record PointPolicyResponse(
        Long pointPolicyId,
        PointPolicyType pointPolicyType,
        RewardType rewardType,
        Integer rewardValue
) {
}
