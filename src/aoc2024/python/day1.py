import duckdb as ddb

query_part1 = """
    with file as (
        select string_split_regex(column0, '\s+') as nums
        from read_csv_auto('resources/2024_1'))
    ,left_nums as (
        select cast(nums[1] as int) as num,
               row_number() over (order by nums[1]) as row
        from file)
    ,right_nums as (
        select cast(nums[2] as int) as num,
               row_number() over (order by nums[2]) as row
        from file)
    ,similarities as (
        select left_nums.num as left_num,
               right_nums.num as right_num,
               abs(left_nums.num - right_nums.num) as similarity
        from left_nums
        join right_nums on left_nums.row = right_nums.row)
    select sum(similarity) as similarity
    from similarities
"""

part1 = ddb.sql(query_part1)

print("Part 1:")
print(part1)

query_part2 = """
    with file as (
        select string_split_regex(column0, '\s+') as nums
        from read_csv_auto('resources/2024_1'))
    ,left_nums as (
        select cast(nums[1] as int) as num,
               row_number() over (order by nums[1]) as row
        from file)
    ,right_nums as (
        select cast(nums[2] as int) as num,
               row_number() over (order by nums[2]) as row
        from file)
    ,similarities as (
        select left_nums.num as num
        from left_nums
        join right_nums on left_nums.num = right_nums.num)
    select sum(num) as similarity
    from similarities
"""

part2 = ddb.sql(query_part2)

print("Part 2:")
print(part2)