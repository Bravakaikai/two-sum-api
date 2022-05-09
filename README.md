# two-sum-api

* Using Spring Boot to create a RESTful web and hosted on Heroku.

* Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.

  Reference from LeetCode problem [1. Two Sum](https://leetcode.com/problems/two-sum/).
  
* Add firebase admin sdk to verify id token.

## API
### POST Method
```
POST https://twosum.herokuapp.com/api/sum
```
#### Request body
> nums ( Type: IntArray )

> target ( Type: Integer )

#### Response
> indices ( Type: IntArray )
