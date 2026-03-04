import pandas as pd

# Load dataset
df = pd.read_csv("data.csv")  

print("===== DATA QUALITY REPORT =====")

# 1. Missing Values
print("\nMissing Values:")
print(df.isnull().sum())

# 2. Negative Sales Check
sales_columns = ['total_sales', 'na_sales', 'jp_sales', 'pal_sales', 'other_sales']

print("\nNegative Sales Check:")
for col in sales_columns:
    if col in df.columns:
        negative_count = (df[col] < 0).sum()
        print(f"Negative values in {col}: {negative_count}")
    else:
        print(f"{col} column not found in dataset.")

# 3. Invalid Release Dates
if 'release_date' in df.columns:
    df['release_date'] = pd.to_datetime(df['release_date'], errors='coerce')
    invalid_dates = df['release_date'].isnull().sum()
    print(f"\nInvalid release dates: {invalid_dates}")
else:
    print("\nNo 'release_date' column found.")

# 4. Duplicate Records
duplicates = df.duplicated().sum()
print(f"\nDuplicate Records: {duplicates}")

print("\n===== END OF REPORT =====")