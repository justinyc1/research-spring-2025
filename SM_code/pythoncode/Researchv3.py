'''
@author Sabeeha Malikah
@date 2/17/25
@Description Post Research Meeting 3
            - This version is able to take in any values for m and d.
            - It computes the U set, B set, V set, E set, and set of all indecomposable tuples.
'''

import math
from itertools import combinations


#This function computes the z mod m z star set given an m value.
def zmodmzstarset(m):
    z_star = []
    for i in range (1,m):
        if(math.gcd(i, m) == 1):
            z_star.append(i)
    return z_star

#This function computes the U set. It does not find the entire U set, only the tuples in the set that satisfy
#the following conditions: 1. the elements of the tuple are ascending 2. the sum of the elements = 0 (mod m)
def u_set(m, d):
    count = 0
    tuple_list = []
    for combo in combinations(range(1, m), 2*d):
        if sum(combo) % m == 0:
            tuple_list.append(combo)
            count += 1

    print("These are the tuple(s) in the U set: ")
    print(tuple_list)
    print("The number of tuple(s) is ",count)
    print()
    return tuple_list

#This function computes the B set. It checks which tuples in the U set satisfy the conditions necessary.
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

#This function computes the E set (identifies exceptional tuples). It also separates tuples that contain some pairs
#and no pairs and returns a list of tuples that contain no pairs. This list is then used in the indecomposable function.
def e_set(m, b_list):
    tuple_list = b_list[:]
    no_pairs = []
    some_pairs = []
    count = len(b_list)
    for b_tuple in b_list:
        pair_count = 0
        for combo in combinations(b_tuple, 2):
            if sum(combo) == m:
                pair_count+=1
        if pair_count == int(len(b_tuple)/2):
            count -= 1
            tuple_list.remove(b_tuple)
        elif pair_count == 0:
            no_pairs.append(b_tuple)
        else:
            some_pairs.append(b_tuple)
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

#This functions determines which tuples in the E set are indecomposable and returns a list of those tuples.
def indecomposable(m, d, no_pairs):
    tuple_list = no_pairs[:]
    for e_tuple in no_pairs:
        for i in range(1, d):
            if e_tuple in no_pairs:
                for combo in combinations(e_tuple, i):
                    if sum(combo) % m == 0:
                        tuple_list.remove(e_tuple)
                        break

    print("These are the exceptional tuple(s) that are indecomposable: ")
    print(tuple_list)
    print("The number of tuple(s) is ", len(tuple_list))
    return tuple_list

#main
m = 19
d = 2
z_star = zmodmzstarset(m)
print("These are are integers in the z mod m z star set for m =", m, z_star, "\n")
u_tuple_list = u_set(m, d)
b_tuple_list = b_set(m, d, u_tuple_list, z_star)
e_tuple_list, no_pairs = e_set(m, b_tuple_list)
indecomposable_list = indecomposable(m, d, no_pairs)

#print summary
print()
print()
print("The number of tuple(s) in the U set is:", len(u_tuple_list))
print("The number of tuple(s) in the V/B set is:", len(b_tuple_list))
print("The number of tuple(s) in the E set is:", len(e_tuple_list))
print("The number of indecomposable tuple(s) is:", len(indecomposable_list))
