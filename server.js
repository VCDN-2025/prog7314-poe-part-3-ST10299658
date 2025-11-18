import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import { db } from './config/firebase-admin.js';
import verifyToken from "./middleware/auth.js";

dotenv.config();

const app = express();
app.use(cors());
app.use(express.json());

// Health check to determine if the API is running
app.get("/", (req, res) => {
  res.json({ 
    success: true,
    message: "Skhaftin API is running!",
    timestamp: new Date().toISOString()
  });
});

// Add these routes that your Kotlin app expects:
app.get("/api/profile", verifyToken, async (req, res) => {
  try {
    const userId = req.user.uid;
    
    // Get user profile from Firestore
    const userDoc = await db.collection('users').doc(userId).get();
    
    if (!userDoc.exists) {
      return res.status(404).json({
        success: false,
        message: "User profile not found"
      });
    }

    res.json({
      success: true,
      data: {
        user: userDoc.data()
      }
    });
  } catch (error) {
    console.error('Profile error:', error);
    res.status(500).json({
      success: false,
      error: "Internal server error"
    });
  }
});

app.post("/api/update-fcm-token", verifyToken, async (req, res) => {
  try {
    const { token } = req.body;
    const userId = req.user.uid;

    if (!token) {
      return res.status(400).json({
        success: false,
        error: "FCM token is required"
      });
    }

    // Update user document with FCM token
    await db.collection('users').doc(userId).set({
      fcmToken: token,
      updatedAt: new Date()
    }, { merge: true });

    res.json({
      success: true,
      message: "FCM token updated successfully"
    });
  } catch (error) {
    console.error('FCM token update error:', error);
    res.status(500).json({
      success: false,
      error: "Failed to update FCM token"
    });
  }
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    success: false,
    error: 'Route not found',
    path: req.path
  });
});

// Start the server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));