'''
@author Sabeeha Malikah
@date 2/18/25
@Description Post Research Meeting 4
            - Goal: make u_set(m, d) more efficient
'''

import math
from itertools import combinations


# This function computes the z mod m z star set given an m value.
def zmodmzstarset(m):
    z_star = []
    for i in range (1,m):
        if(math.gcd(i, m) == 1):
            z_star.append(i)
    return z_star

# This function computes the U set. It does not find the entire U set, only the tuples in the set that satisfy
# the following conditions: 1. the elements of the tuple are ascending 2. the sum of the elements = 0 (mod m)
# 3. The tuple is indecomposable.
def u_set(m, d):
    count = 0
    tuple_list = []
    for combo in combinations(range(1, m), 2*d):
        decomposable = False
        if sum(combo) % m == 0:
            for i in range(2, d, 2):
                if not decomposable:
                    for sub_combo in combinations(combo, i):
                        if sum(sub_combo) % m == 0:
                            decomposable = True
                            break
            if not decomposable:
                tuple_list.append(combo)
                print(combo)
                count += 1
    print("These are the tuple(s) in the U set: ")
    # print(tuple_list)
    print("The number of tuple(s) is ",count)
    print()
    return tuple_list

# This function computes the B set. It checks which tuples in the U set satisfy the conditions necessary.
def b_set(m, d, u_list, z_star):
    tuple_list = []
    count = 0
    for u_tuple in u_list:
        t_count = 0
        for t in z_star:
            i = 0
            sum = 0
            while i < 2*d:
                sum += ((u_tuple[i] * t) % m)
                i += 1
            if (sum/m) == d:
                t_count = t_count+1
        if t_count == len(z_star):
            tuple_list.append(u_tuple)
            count = count+1
    print("These are the tuple(s) in the V/B set: ")
    print(tuple_list)
    print("The number of tuple(s) is ", count)
    print()
    return tuple_list

# main
m = 15
d = 4
z_star = zmodmzstarset(m)
print("These are are integers in the z mod m z star set for m =", m, z_star, "\n")
u_tuple_list = u_set(m, d)
b_tuple_list = b_set(m, d, u_tuple_list, z_star)

#print summary
print()
print("For m =", m, "and d =", d, ":")
print("The number of tuple(s) in the U set is:", len(u_tuple_list))
print("The number of indecomposable tuple(s) is:", len(b_tuple_list))