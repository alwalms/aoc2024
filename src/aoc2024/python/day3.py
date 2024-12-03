import duckdb as ddb

query_part1 = """
    with file as (
        select * as nums
        from read_csv_auto('resources/2024_3')
        ),
    mul as (
        select unnest(regexp_extract_all(column0, 'mul\(\d+,\d+\)')) as row
        from file
        ),
    products as (
        select regexp_extract_all(row, '\d+')[1]::int * regexp_extract_all(row, '\d+')[2]::int as multiplier
        from mul
        )
    select sum(multiplier) as total
    from products
"""

part1 = ddb.sql(query_part1)

print("Part 1:")
print(part1)

# query_part2 = """
#     with file as (
#         select *
#         from read_csv_auto('resources/2024_3')
#         ),
#     dos as (
#         select unnest(string_split_regex(column0, 'do()')) as row
#         from file
#         ),
#     mul as (
#         select unnest(regexp_extract_all(column0, 'mul\(\d+,\d+\)')) as row
#         from file
#         ),
#     products as (
#         select regexp_extract_all(row, '\d+')[1]::int * regexp_extract_all(row, '\d+')[2]::int as multiplier
#         from mul
#         )
#     select *
#     from dos

# """

# part2 = ddb.sql(query_part2)

# print("Part 2:")
# print(part2)