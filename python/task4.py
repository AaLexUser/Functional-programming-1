def is_palindrome(n):
    return str(n) == str(n)[::-1]


def largest_palindrome_product():
    max_palindrome = 0
    for i in range(999, 99, -1):
        for j in range(999, 99, -1):
            product = i * j
            if product > max_palindrome and is_palindrome(product):
                max_palindrome = product
    return max_palindrome


if __name__ == "__main__":
    print(f"Largest palindrome: {largest_palindrome_product()}")
