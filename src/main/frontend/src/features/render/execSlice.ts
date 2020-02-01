import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface NodePosition {
  id: string;
  x: number;
  y: number;
}

export type EventBusStatus = 'open' | 'closed';
type AlchemistStatus = 'INIT' | 'READY' | 'PAUSED' | 'RUNNING' | 'TERMINATED';
export type ExecutionStatus = 'disconnected' | 'connecting' | AlchemistStatus;

interface ExecState {
  connection: EventBusStatus;
  execution: {
    drawing: NodePosition[] | null;
    id: string | null;
    status: ExecutionStatus;
  };
}

const initialState: ExecState = {
  connection: 'closed',
  execution: {
    drawing: null,
    id: null,
    status: 'disconnected',
  },
};

const execSlice = createSlice({
  name: 'exec',
  initialState,
  reducers: {
    changeStatus(state, action: PayloadAction<EventBusStatus>): void {
      state.connection = action.payload;
    },
    drawInit(state, action: PayloadAction<NodePosition[]>): void {
      state.execution = {
        status: 'RUNNING',
        drawing: action.payload,
        id: state.execution.id,
      };
    },
    drawStep(state, action: PayloadAction<NodePosition[]>): void {
      state.execution = {
        status: state.execution.status,
        drawing: action.payload,
        id: state.execution.id,
      };
    },
    drawEnd(state, action: PayloadAction<NodePosition[]>): void {
      state.execution = {
        status: 'TERMINATED',
        drawing: action.payload,
        id: state.execution.id,
      };
    },
  },
});

export const {
  changeStatus,
  drawEnd,
  drawInit,
  drawStep,
} = execSlice.actions;

export default execSlice.reducer;
