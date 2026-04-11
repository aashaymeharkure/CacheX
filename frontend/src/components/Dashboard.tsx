"use client";

import React, { useEffect, useState } from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export default function Dashboard() {
  const [metrics, setMetrics] = useState<any>(null);
  const [history, setHistory] = useState<any[]>([]);
  const [config, setConfig] = useState({
    mode: "random",
    speed: 10,
    cacheSize: 5,
    custom: "",
  });

  const [connected, setConnected] = useState(false);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws-metrics"),
      onConnect: () => {
        setConnected(true);
        client.subscribe("/topic/metrics", (message) => {
          const body = JSON.parse(message.body);
          setMetrics(body);
          setHistory((prev) => [...prev, body].slice(-100)); // Keep last 100 entries for graph
        });
      },
      onDisconnect: () => {
        setConnected(false);
      },
      onWebSocketClose: () => {
        setConnected(false);
      }
    });
    client.activate();
    return () => {
      client.deactivate();
    };
  }, []);

  const sendCommand = async (command: string, body?: any) => {
    try {
      await fetch(`http://localhost:8080/api/simulation/${command}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: body ? JSON.stringify(body) : undefined,
      });
    } catch (err) {
      alert(`Backend connection failed! Is the Java server running on port 8080?\n\nError: ${err}`);
      console.error(err);
    }
  };

  return (
    <div className="grid grid-cols-3 grid-rows-2 h-full gap-4 p-4">
      {/* Panel A: Request Stream */}
      <div className="border border-zinc-800 rounded bg-zinc-950 p-4 overflow-hidden flex flex-col font-mono">
        <h2 className="text-zinc-400 mb-2">Panel A. Request Stream</h2>
        <div className="text-xl">Req Count: {metrics?.requestCount || 0}</div>
        <div className="flex-1 mt-4 overflow-y-auto opacity-50">
           Stream entries mock...
        </div>
      </div>

      {/* Panel B: Cache State */}
      <div className="border border-zinc-800 rounded bg-zinc-950 p-4 font-mono">
        <h2 className="text-zinc-400 mb-2">Panel B. Cache State (Hybrid)</h2>
        <div>Size: {config.cacheSize}</div>
        <div>Total Evictions: {metrics?.hybridEvictions || 0}</div>
      </div>

      {/* Panel C: Decision Engine */}
      <div className="border border-zinc-800 rounded bg-zinc-950 p-4 font-mono">
        <h2 className="text-zinc-400 mb-2">Panel C. Decision Engine</h2>
        <div className="mb-4">Entropy Score: {metrics?.entropy?.toFixed(2) || "0.00"}</div>
        <div className="w-full bg-zinc-800 h-6 flex overflow-hidden mb-2 rounded">
          <div
            className="bg-zinc-200 h-full"
            style={{ width: `${(metrics?.alpha || 0.5) * 100}%` }}
          />
          <div
            className="bg-zinc-600 h-full"
            style={{ width: `${(metrics?.beta || 0.5) * 100}%` }}
          />
        </div>
        <div className="flex justify-between text-xs">
          <span>LFU (α={metrics?.alpha?.toFixed(2)})</span>
          <span>LRU (β={metrics?.beta?.toFixed(2)})</span>
        </div>
      </div>

      {/* Panel D: Prediction */}
      <div className="border border-zinc-800 rounded bg-zinc-950 p-4 font-mono">
        <h2 className="text-zinc-400 mb-2">Panel D. Prediction</h2>
        <div className="text-lg">Accuracy: {(metrics?.predictionAccuracy * 100)?.toFixed(1) || 0}%</div>
        <div className="text-lg text-yellow-500">Prefetch Hit Rate: {(metrics?.prefetchHitRate * 100)?.toFixed(1) || 0}%</div>
        <div className="mt-4 text-sm text-zinc-500">
           Last Prediction: {metrics?.nextPrediction || "N/A"}<br/>
           Confirmed Patterns: {metrics?.detectedPatterns?.join(" | ") || "None"}
        </div>
      </div>

      {/* Panel E: Benchmark Graph */}
      <div className="border border-zinc-800 rounded bg-zinc-950 p-4 font-mono">
        <h2 className="text-zinc-400 mb-2">Panel E. Benchmark Graph</h2>
        <ResponsiveContainer width="100%" height="85%">
          <LineChart data={history}>
            <CartesianGrid strokeDasharray="3 3" stroke="#333" />
            <XAxis dataKey="requestCount" stroke="#888" tick={{fontSize: 10}}/>
            <YAxis domain={[0, 1]} stroke="#888" tick={{fontSize: 10}}/>
            <Tooltip contentStyle={{backgroundColor: '#000', border: 'none'}}/>
            <Legend />
            <Line type="step" dataKey="lruHitRate" stroke="#666" name="LRU" dot={false} isAnimationActive={false} />
            <Line type="step" strokeDasharray="5 5" dataKey="lfuHitRate" stroke="#aaa" name="LFU" dot={false} isAnimationActive={false}/>
            <Line type="monotone" dataKey="hybridHitRate" stroke="#fff" strokeWidth={2} name="Hybrid" dot={false} isAnimationActive={false}/>
          </LineChart>
        </ResponsiveContainer>
      </div>

      {/* Panel F: Controls */}
      <div className="border border-zinc-800 rounded bg-zinc-950 p-4 font-mono flex flex-col justify-between relative">
        <div className="flex justify-between items-center mb-2">
          <h2 className="text-zinc-400">Panel F. Controls</h2>
          <span className={`text-xs px-2 py-1 rounded ${connected ? "bg-green-900 text-green-400" : "bg-red-900 text-red-400"}`}>
            {connected ? "● BACKEND CONNECTED" : "○ BACKEND DISCONNECTED"}
          </span>
        </div>
        
        <div className="grid grid-cols-2 gap-4">
          <label className="flex flex-col">Speed (Req/s)
            <input type="range" min="1" max="50" value={config.speed} 
              onChange={e => setConfig({...config, speed: Number(e.target.value)})} 
              className="mt-2"/>
          </label>
          <label className="flex flex-col">Mode
            <select value={config.mode} onChange={e => setConfig({...config, mode: e.target.value})} 
              className="mt-2 bg-zinc-900 p-2 rounded">
              <option value="random">Random</option>
              <option value="pattern">Pattern Loop</option>
              <option value="zipfian">Zipfian</option>
            </select>
          </label>
        </div>

        <div className="flex gap-4 mt-6">
          <button onClick={() => sendCommand("config", config).then(() => sendCommand("start"))} className="flex-1 bg-white text-black py-2 rounded font-bold hover:bg-zinc-200">START</button>
          <button onClick={() => sendCommand("pause")} className="flex-1 border border-zinc-600 text-zinc-300 py-2 rounded hover:bg-zinc-800">PAUSE</button>
          <button onClick={() => sendCommand("reset")} className="flex-1 border border-red-900 text-red-500 py-2 rounded hover:bg-red-950">RESET</button>
        </div>
      </div>
    </div>
  );
}
