import json
import logging
import math

from collections import deque
import random

def generate_b_model_trace(b, n, total_volume, num_ticks):
    if not (0.5 <= b <= 1.0):
        raise ValueError("Bias parameter (b) must be between 0.5 and 1.0 to replicate LRD and self-similarity")
    
    trace_length = 1 << n
    output_trace = [0.0] * trace_length
    output_idx = 0

    stack = deque()
    stack.append((0, float(total_volume)))
    
    logging.info(f"Starting b-model generation: b={b}, n={n} (len={trace_length}), N={total_volume}")
    while stack:
        k, v = stack.pop()
        if k == n:
            if output_idx < trace_length:
                output_trace[output_idx] = v
                output_idx += 1
            else:
                break
            continue
        is_heads = random.random() < 0.5
        if is_heads:
            v_left = v * b
            v_right = v * (1.0 - b)
        else:
            v_left = v * (1.0 - b)
            v_right = v * b
        stack.append((k + 1, v_right))
        stack.append((k + 1, v_left))
    
    # if trace is shorter than num_ticks due to n
    if output_idx != trace_length and output_idx < num_ticks :
        if output_idx > 0:
            output_trace[output_idx:] = [output_trace[output_idx-1]] * (trace_length - output_idx)
        # if output_idx is 0, means trace_length was 0 (n was too small for num_ticks)
        else:
            output_trace = [0.0] * trace_length
    
    return output_trace[:num_ticks]

def scale_trace_to_users(yt_trace, max_users, min_users=1):
    max_yt = max(yt_trace) if yt_trace else 0.0
    
    if max_yt == 0:
        return [min_users] * len(yt_trace)
    
    target_users = [max(min_users, round((yt / max_yt) * max_users)) for yt in yt_trace]
    
    return target_users

def stretch_user_profile(original_scaled_profile, period_per_tick):
    stretched_profile = []

    for user_count in original_scaled_profile:
        stretched_profile.extend([user_count] * period_per_tick)
    
    return stretched_profile

#! B-Model Generation Parameters
B_MODEL_BIAS = 0.705 # Taken from the 2002 CMU b-model paper
MAX_CONCURRENT_USERS = 5000 # Based on preliminary test runs
MIN_CONCURRENT_USERS = 100
TOTAL_VOLUME_N = 1_000_000

SECONDS_PER_MINUTE = 60
TEST_TIME_IN_MINS = 20
PERIOD_PER_BMODEL_TICK_SECS = 10 # Based on preliminary results showing 10 seconds to complete user flow

OUTPUT_PROFILE_FILENAME = "bmodel_user_profile.json"

def main():
    logging.basicConfig(level=logging.INFO)
    logging.info("Starting B-Model Profile Generation...")

    test_time_in_secs = TEST_TIME_IN_MINS * SECONDS_PER_MINUTE
    num_b_model_ticks = math.ceil(test_time_in_secs / PERIOD_PER_BMODEL_TICK_SECS)
    aggregation_level_n = math.ceil(math.log2(num_b_model_ticks)) if num_b_model_ticks > 0 else 1
    if aggregation_level_n < 1:
        aggregation_level_n = 1

    logging.info(f"Calculated AGGREGATION_LEVEL_N for b-model: {aggregation_level_n}")

    raw_trace = generate_b_model_trace(
        B_MODEL_BIAS,
        aggregation_level_n, 
        TOTAL_VOLUME_N,
        num_b_model_ticks
    )

    scaled_counts = scale_trace_to_users(
        raw_trace,
        MAX_CONCURRENT_USERS,
        MIN_CONCURRENT_USERS
    )

    final_stretched_profile = stretch_user_profile(
        scaled_counts,
        PERIOD_PER_BMODEL_TICK_SECS
    )
    
    logging.info(f"Final stretched profile length: {len(final_stretched_profile)} seconds")

    with open(OUTPUT_PROFILE_FILENAME, 'w') as f:
        json.dump(final_stretched_profile, f)
    logging.info(f"Successfully generated and saved user profile to {OUTPUT_PROFILE_FILENAME}")

if __name__ == "__main__":
    main()
