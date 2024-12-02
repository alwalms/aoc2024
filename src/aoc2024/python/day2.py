import duckdb as ddb

query_part1 = """
    WITH file AS (
        SELECT 
            row_number() OVER () AS row_id, 
            string_split_regex(column0, '\s+')::INTEGER[] AS nums
        FROM read_csv_auto('resources/2024_2')
    ),
    unnested AS (
        SELECT 
            row_id,
            num,
            ROW_NUMBER() OVER (PARTITION BY row_id ORDER BY unnest_id) AS pos
        FROM (
            SELECT 
                row_id, 
                unnest(nums) AS num, 
                ROW_NUMBER() OVER (PARTITION BY row_id) AS unnest_id
            FROM file
        ) subquery
    ),
    differences AS (
        SELECT 
            row_id,
            pos,
            num,
            ABS(LEAD(num) OVER (PARTITION BY row_id ORDER BY pos) - num) AS diff,
            CASE 
                WHEN LEAD(num) OVER (PARTITION BY row_id ORDER BY pos) < num THEN 1 ELSE 0 
            END AS is_descending,
            CASE 
                WHEN LEAD(num) OVER (PARTITION BY row_id ORDER BY pos) > num THEN 1 ELSE 0 
            END AS is_ascending
        FROM unnested
    ),
    aggregates AS (
        SELECT 
            row_id,
            SUM(CASE WHEN diff NOT BETWEEN 1 AND 3 THEN 1 ELSE 0 END) AS invalid_differences,
            SUM(is_descending) AS count_descending,
            SUM(is_ascending) AS count_ascending,
            COUNT(*)-1 AS count_total_minus_one
        FROM differences
        GROUP BY row_id
    ),
    results AS (
        SELECT 
            row_id,
            CASE 
                WHEN 
                invalid_differences = 0 AND 
                (count_descending = 0 OR count_descending = count_total_minus_one) AND 
                (count_ascending = 0 OR count_ascending = count_total_minus_one) THEN 'valid'
                ELSE 'invalid'
            END AS status
        FROM aggregates
    )
    SELECT count(*) as answer
    FROM results
    WHERE status = 'valid'
    LIMIT 100
"""

part1 = ddb.sql(query_part1)

print("Part 1:")
print(part1)

query_part2 = """
    WITH file AS (
        SELECT 
            row_number() OVER () AS row_id, 
            string_split_regex(column0, '\s+')::INTEGER[] AS nums
        FROM read_csv_auto('resources/2024_2')
    ),
    unnested AS (
        SELECT 
            row_id,
            num,
            ROW_NUMBER() OVER (PARTITION BY row_id ORDER BY unnest_id) AS pos
        FROM (
            SELECT 
                row_id, 
                unnest(nums) AS num, 
                ROW_NUMBER() OVER (PARTITION BY row_id) AS unnest_id
            FROM file
        ) subquery
    ),
    differences AS (
        SELECT 
            row_id,
            pos,
            num,
            ABS(LEAD(num) OVER (PARTITION BY row_id ORDER BY pos) - num) AS lead_diff,
            ABS(LAG(num) OVER (PARTITION BY row_id ORDER BY pos) - num) AS lag_diff,
            CASE 
                WHEN LEAD(num) OVER (PARTITION BY row_id ORDER BY pos) < num THEN 1 ELSE 0 
            END AS is_descending,
            CASE 
                WHEN LAG(num) OVER (PARTITION BY row_id ORDER BY pos) > num THEN 1 ELSE 0 
            END AS is_ascending
        FROM unnested
    ),
    validation AS (
        SELECT 
            row_id,
            pos,
            num,
            CASE 
                -- Check if removing the current number makes the sequence valid
                WHEN 
                    (SUM(CASE WHEN lead_diff NOT BETWEEN 1 AND 3 THEN 1 ELSE 0 END) 
                    OVER (PARTITION BY row_id) <= 1) AND
                    (SUM(CASE WHEN lag_diff NOT BETWEEN 1 AND 3 THEN 1 ELSE 0 END) 
                    OVER (PARTITION BY row_id) <= 1) AND
                    ((SUM(is_descending) OVER (PARTITION BY row_id) <= 1) OR
                    (SUM(is_ascending) OVER (PARTITION BY row_id) <= 1))
                THEN 1
                ELSE 0
            END AS valid_after_removal
        FROM differences
    ),
    results AS (
        SELECT 
            row_id,
            MAX(valid_after_removal) AS is_valid
        FROM validation
        GROUP BY row_id
    )
    SELECT *
    FROM validation
    LIMIT 100;
"""

part2 = ddb.sql(query_part2)

print("Part 2:")
print(part2)