import json
import random
import string
import logging
import time

from locust import (
    FastHttpUser,
    task, 
    SequentialTaskSet,
)

from bmodel import BModelShape

#! Custom load shape
class FlowTestShape(BModelShape):
    pass

###! UTILS
def generate_random_string(length: int):
    characters = string.ascii_letters + string.digits
    random_string = ''.join(random.choice(characters) for _ in range(length))
    return random_string

def construct_endpoint(path: str):
    API_VERSION = "/api/v1"
    return f"{API_VERSION}{path}"
###!

###! TASK SETS
class BrowseProductsTaskSet(SequentialTaskSet):
    @task
    def view_all_products(self):
        self.client.get(
            url=construct_endpoint(f"/products"),
            name="/products",
            headers=self.user.default_headers,
        )

    @task
    def view_product(self):
        product_id = random.randint(1, 10)
        
        self.client.get(
            url=construct_endpoint(f"/products/{product_id}"), 
            name="/products/:id",
            headers=self.user.default_headers,
        )
        
    @task
    def stop(self):
        self.interrupt()

class PlaceOrderTaskSet(SequentialTaskSet):
    @task
    def view_product(self):
        product_id = 1
        
        self.client.get(
            url=construct_endpoint(f"/products/{product_id}"), 
            name="/products/:id",
            headers=self.user.default_headers,
        )

    @task
    def place_order(self):
        quantity = random.randint(1, 10)
        place_order_payload = {
            "orderItems": [
                {
                    "productId": 1,
                    "quantity": quantity
                }
            ]
        }

        self.client.post(
            url=construct_endpoint("/orders"),
            name="/orders",
            headers=self.user.default_headers,
            json=place_order_payload,
        )

    @task
    def stop(self):
        self.interrupt()
###!

###! USER
class FlowUser(FastHttpUser):
    tasks = {
        BrowseProductsTaskSet: 30,
        PlaceOrderTaskSet: 1,
    }

    def wait_time(self):
        # Janevski Pareto wait time to simulate a heavy-tailed, Pareto distibution
        alpha = 1.6
        xm = 1.875

        return random.paretovariate(alpha) * xm
    
    jwt_token = None
    user_id = generate_random_string(10)
    email = f"user_{user_id}@email.com"
    password = generate_random_string(12)

    def on_start(self):
        """
        For each simulated User, locust should first register, login, 
        and stores the auth information
        """
        JWT_DATA_TOKEN_FIELD = "token"
        self.user_id = generate_random_string(10)
        self.password = generate_random_string(12)
        self.email = f"user_{self.user_id}@email.com"

        try:
            register_payload = {
                "email": self.email,
                "password": self.password,
                "firstName": f"User {self.user_id}"
            }

            with self.client.post(construct_endpoint("/auth/register"), json=register_payload, catch_response=True) as response:
                if not response.ok:
                    response.failure(f"[{response.status_code}] Failed to register user {self.email}.")
                    return
                else:
                    response.success()
            
            signin_payload = {
                "email": self.email,
                "password": self.password
            }

            signin_url = construct_endpoint("/auth/signin")
            with self.client.post(signin_url, json=signin_payload, catch_response=True) as response:
                if not response.ok:
                    response.failure(f"[{response.status_code}] Failed to sign in user {self.email}.")
                    return

                if response.status_code == 0 or response.text is None:
                    response.failure(f"Sign in failed: Status Code {response.status_code}, No response body. Error: {response.error if hasattr(response, 'error') else 'N/A'}")
                    return

                try:
                    jwt_data = response.json()
                except json.JSONDecodeError as e:
                    response.failure(f"Failed to decode JSON from sign in response. Status: {response.status_code}, Text: '{response.text}'. Error: {e}")

                jwt_data = response.json()

                token = jwt_data.get(JWT_DATA_TOKEN_FIELD)

                if not token:
                    response.failure(f"Failed to get JWT token for {self.email}, 'token' not found in response")
                    return
                
                self.jwt_token = token
                self.default_headers = {
                    "Authorization": f"Bearer {self.jwt_token}"
                }

                response.success()
        except Exception as e:
            logging.error(f"Exception during on_start for {self.email}: {e}")
###! 
