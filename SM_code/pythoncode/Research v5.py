"""
@author Sabeeha Malikah
@date 3/9/25
@Description Post Meeting on 3/3/25
    Changes:
        - The U set only generates tuples whose dth element is <= (m-1)/2
            * This was an observation made by Justin about the V set (we have not proven it yet).

"""

import math
from itertools import combinations

"""
This function computes the z mod m z star set given an m value. This set contains integers ranging from [1,m).
The integers in this set satisfy the following property: gcd(i, m) == 1.

INPUT:
- ``m`` -- an odd integer which represents the degree in C_m: y^2=x^m-1

OUTPUT:
- ``z_star`` -- The set representing the z mod m z star set for the given m value.
"""
def zmodmzstarset(m):
    z_star = []
    for i in range (1,m):
        if(math.gcd(i, m) == 1):
            z_star.append(i)
    return z_star

"""
This function computes the U set for a given m and d value. It does not find the entire U set, only the tuples in the set 
that satisfy the following conditions: 
    1. the elements of the tuple are ascending 
    2. the sum of the elements = 0 (mod m)

INPUT:
- ``m`` -- an odd integer which represents the degree in C_m: y^2=x^m-1
- ``d``-- an integer whose range is [1, (m-1)/2]. This integer defines the length of the tuples produced.
          the tuples have length 2*d.

OUTPUT:
- ``tuple_list`` -- a list represent the U set for the given m and d values. This is a restricted U set (see properties
                    of the tuples above). 
"""
def u_set(m, d):
    count = 0
    tuple_list = []
    print("These are the tuple(s) in the U set: ")
    for combo in combinations(range(1, m), 2*d):
        if combo[d-1] <= ((m-1)/2):
            if sum(combo) % m == 0:
                tuple_list.append(combo)
                print(combo)
                count += 1

    # print("These are the tuple(s) in the U set: ")
    # print(tuple_list)
    print("The number of tuple(s) is ",count)
    print()
    return tuple_list

"""
This function computes the V set. It checks which tuples in the U set satisfy the necessary conditions.

INPUT:
- ``m`` -- an odd integer which represents the degree in C_m: y^2=x^m-1
- ``d``-- an integer whose range is [1, (m-1)/2]. This integer defines the length of the tuples produced.
          the tuples have length 2*d.
- ``u_list`` -- a list representing the U set for the given m and d values that was returned by the u_set() function.
- ``z_star`` -- a list representing the z mod m z star set for the given m value that was returned by the zmodmzstar() function.

OUTPUT:
- ``tuple_list`` -- a list represent the V set for the given m and d values.

"""
def v_set(m, d, u_list, z_star):
    tuple_list = []
    count = 0
    print("These are the tuple(s) in the V/B set: ")
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
    # print("These are the tuple(s) in the V/B set: ")
    print(tuple_list)
    print("The number of tuple(s) is ", count)
    print()
    return tuple_list

"""
This function computes the E set (set containing exceptional tuples). It creates the following lists:
    1. no_pairs -- represents the set with tuples that contain no pairs of elements that add to m.
    2. some_pairs -- represents the set with tuples that contain some but not all pairs of elements that add to m.

INPUT:
- ``m`` -- an odd integer which represents the degree in C_m: y^2=x^m-1
- ``v_list`` -- a list representing the V set for the given m and d values that was returned by the v_set() function.

OUTPUT:
- ``tuple_list`` -- a list representing the set of all tuples in the V set that are exceptional cycles
- ``no_pairs`` -- a list representing the subset of the exceptional cycles set that contains tuples containing no pairs 
"""
def e_set(m, v_list):
    tuple_list = v_list[:]
    no_pairs = []
    some_pairs = []
    count = len(v_list)
    # This checks for all possible pair combinations in the tuple.
    for v_tuple in v_list:
        pair_count = 0
        for combo in combinations(v_tuple, 2):
            if sum(combo) == m:
                pair_count+=1
        if pair_count == int(len(v_tuple)/2):
            count -= 1
            tuple_list.remove(v_tuple)
        elif pair_count == 0:
            no_pairs.append(v_tuple)
        else:
            some_pairs.append(v_tuple)
    print("These are the tuple(s) in the E set: ")
    print(tuple_list)
    print("The number of tuple(s) is ", count)
    print("These tuples have no pairs that add to m =", m,)
    print(no_pairs)
    print("The number of tuple(s) is ", len(no_pairs))
    print("These tuples have some (but not all) pairs that add to m =", m, )
    print(some_pairs)
    print("The number of tuple(s) is ", len(some_pairs))
    print()
    return tuple_list, no_pairs

"""
This functions determines which tuples in the no_pairs set are indecomposable and returns a list of those tuples.

INPUT: 
- ``m`` -- an odd integer which represents the degree in C_m: y^2=x^m-1
- ``d``-- an integer whose range is [1, (m-1)/2]. This integer defines the length of the tuples produced.
          the tuples have length 2*d.
- ``no_pairs`` -- a list representing the subset of the exceptional cycles set that contains tuples containing no pairs
                  returned by the e_set() function
                  
OUTPUT:
- ``tuple_list`` -- a list representing a subset of the no pairs set containing the tuples that are indecomposable
"""
def indecomposable(m, d, no_pairs):
    tuple_list = no_pairs[:]
    for e_tuple in no_pairs:
        for i in range(2, d, 2):
            if e_tuple in no_pairs:
                for combo in combinations(e_tuple, i):
                    if sum(combo) % m == 0:
                        tuple_list.remove(e_tuple)
                        break

    print("These are the exceptional tuple(s) that are indecomposable: ")
    print(tuple_list)
    print("The number of tuple(s) is ", len(tuple_list))
    return tuple_list

def main():
    m = 105
    d = 4
    filename = f"m_{m}_d_{d}_output.txt"
    # with open(filename, "w") as file:
    #     file.write(f"These are the tuples for m = {m} and d = {d}")
    z_star = zmodmzstarset(m)
    with open(filename, "w") as file:
        file.write(f"For m = {m} and d = {d}\n")
        file.write(f"These are integers in the z mod m z star set for m = {m}\n {z_star}\n")
    u_tuple_list = u_set(m, d)
    v_tuple_list = v_set(m, d, u_tuple_list, z_star)
    e_tuple_list, no_pairs = e_set(m, v_tuple_list)
    indecomposable_list = indecomposable(m, d, no_pairs)

    # print summary & tuples
    with open(filename, "a") as file:
        # printing summary
        file.write(f"The number of tuple(s) in the U set is: {len(u_tuple_list)}\n")
        file.write(f"The number of tuple(s) in the V/B set is: {len(v_tuple_list)}\n")
        file.write(f"The number of tuple(s) in the E set is: {len(e_tuple_list)}\n")
        file.write(f"The number of tuple(s) with no pairs is: {len(no_pairs)}\n")
        # file.write(no_pairs)
        file.write(f"The number of indecomposable tuple(s) is: {len(indecomposable_list)}\n\n")

        # printing tuples
        file.write(f"The tuples in the V set are:\n")
        for x in v_tuple_list:
            file.write(f"{x}\n")
        file.write(f"The number of tuple(s) in the V set is: {len(v_tuple_list)}\n")
        file.write(f"\n")
        # file.write(f"The tuples in the E set are:\n")
        # for x in e_tuple_list:
        #     file.write(f"{x}\n")
        # file.write(f"The number of tuple(s) in the E set is: {len(e_tuple_list)}\n")
        # file.write(f"The number of tuple(s) with no pairs is: {len(no_pairs)}\n")
        # file.write(no_pairs)
        file.write(f"The indecomposable tuples are:\n")
        for x in indecomposable_list:
            file.write(f"{x}\n")
        file.write(f"The number of indecomposable tuple(s) is: {len(indecomposable_list)}\n")
main()
