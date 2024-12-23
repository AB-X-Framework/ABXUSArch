package org.abx.ws.frames;

/**
 *
 * @author luis
 */
public enum FrameType {
  Continuation,
  Text,
  Binary,
  Ping,
  Pong,
  ConnectionClose,
  Unknown
}