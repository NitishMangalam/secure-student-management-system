import asyncio
from fastapi import FastAPI
from aiokafka import AIOKafkaConsumer

app = FastAPI()

# Configuration - Matches your Java Kafka setup
KAFKA_TOPIC = "student-topic"
KAFKA_BOOTSTRAP_SERVERS = "localhost:9092"

async def ai_career_advisor():
    # This consumer joins the group to read student registration events
    consumer = AIOKafkaConsumer(
        KAFKA_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id="ai-service-group",
        auto_offset_reset='earliest'
    )
    await consumer.start()
    print("🤖 AI Career Advisor is ONLINE and listening to Kafka...")
    
    try:
        async for msg in consumer:
            # 1. Receive the message from Java
            data = msg.value.decode('utf-8')
            print(f"📩 New Student Event Received: {data}")

            # 2. Simple AI Logic
            # We look for the student name in your message format
            if "Created:" in data:
                student_name = data.split("Created:")[-1].strip()
                advice = f"AI Recommendation for {student_name}: Based on your profile, start with 'Java Microservices' and 'Spring Security' path."
                print(f"💡 {advice}")
                
    finally:
        await consumer.stop()

@app.on_event("startup")
async def startup_event():
    # This runs the listener in the background when the server starts
    asyncio.create_task(ai_career_advisor())

@app.get("/")
async def health_check():
    return {"status": "AI Service is Healthy", "listening_to": KAFKA_TOPIC}