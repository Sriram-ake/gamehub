/**
 * GameHub - Tic Tac Toe Game
 * Neon 3x3 grid with Minimax AI opponent, winning line animations, and GameBase hooks.
 */

import { GameBase, GameState } from '../engine/GameBase.js';

export class TicTacToeGame extends GameBase {
  constructor(canvasId = 'game-canvas') {
    super(canvasId, 'tictactoe', 800, 600);

    this.gridSize = 3;
    this.board = Array(9).fill(null);
    this.turn = 'X'; // X is Player (cyan), O is AI (purple)
    this.winner = null;
    this.winningCombo = null;

    this.boardSize = 360;
    this.cellSize = this.boardSize / 3; // 120px
    this.boardX = (this.virtualWidth - this.boardSize) / 2;
    this.boardY = 120;

    this.particles = [];
    this.hoverCell = -1;

    this._bindPointerEvents();
  }

  _bindPointerEvents() {
    this.canvas.addEventListener('mousemove', (e) => {
      if (this.state !== GameState.PLAYING || this.winner || this.turn !== 'X') {
        this.hoverCell = -1;
        return;
      }
      const rect = this.canvas.getBoundingClientRect();
      const scaleX = this.virtualWidth / rect.width;
      const scaleY = this.virtualHeight / rect.height;
      const mouseX = (e.clientX - rect.left) * scaleX;
      const mouseY = (e.clientY - rect.top) * scaleY;

      this.hoverCell = this.getCellAt(mouseX, mouseY);
    });

    this.canvas.addEventListener('click', (e) => {
      if (this.state !== GameState.PLAYING || this.winner || this.turn !== 'X') return;

      const rect = this.canvas.getBoundingClientRect();
      const scaleX = this.virtualWidth / rect.width;
      const scaleY = this.virtualHeight / rect.height;
      const mouseX = (e.clientX - rect.left) * scaleX;
      const mouseY = (e.clientY - rect.top) * scaleY;

      const cellIndex = this.getCellAt(mouseX, mouseY);
      if (cellIndex !== -1 && this.board[cellIndex] === null) {
        this.playerMove(cellIndex);
      }
    });
  }

  getCellAt(x, y) {
    if (x < this.boardX || x > this.boardX + this.boardSize ||
        y < this.boardY || y > this.boardY + this.boardSize) {
      return -1;
    }
    const col = Math.floor((x - this.boardX) / this.cellSize);
    const row = Math.floor((y - this.boardY) / this.cellSize);
    return row * 3 + col;
  }

  init() {
    this.reset();
  }

  reset() {
    this.score = 0;
    this.level = 1;
    this.board = Array(9).fill(null);
    this.turn = 'X';
    this.winner = null;
    this.winningCombo = null;
    this.particles = [];
  }

  playerMove(index) {
    this.board[index] = 'X';
    this.spawnCellParticles(index, '#00e5ff');
    this.checkStatus();

    if (!this.winner) {
      this.turn = 'O';
      setTimeout(() => this.aiMove(), 350);
    }
  }

  aiMove() {
    if (this.winner || this.state !== GameState.PLAYING) return;

    // AI logic: check win, then block, then center, then corners, then random
    let bestMove = this.findBestMove();
    if (bestMove !== -1) {
      this.board[bestMove] = 'O';
      this.spawnCellParticles(bestMove, '#9d4edd');
      this.checkStatus();
      if (!this.winner) {
        this.turn = 'X';
      }
    }
  }

  findBestMove() {
    const lines = [
      [0,1,2],[3,4,5],[6,7,8],
      [0,3,6],[1,4,7],[2,5,8],
      [0,4,8],[2,4,6]
    ];

    // 1. Can AI win right now?
    for (let combo of lines) {
      const [a, b, c] = combo;
      const vals = [this.board[a], this.board[b], this.board[c]];
      if (vals.filter(v => v === 'O').length === 2 && vals.includes(null)) {
        return combo[vals.indexOf(null)];
      }
    }

    // 2. Can player win? Block it!
    for (let combo of lines) {
      const [a, b, c] = combo;
      const vals = [this.board[a], this.board[b], this.board[c]];
      if (vals.filter(v => v === 'X').length === 2 && vals.includes(null)) {
        return combo[vals.indexOf(null)];
      }
    }

    // 3. Take center if available
    if (this.board[4] === null) return 4;

    // 4. Take random available corner
    const corners = [0, 2, 6, 8].filter(i => this.board[i] === null);
    if (corners.length > 0) {
      return corners[Math.floor(Math.random() * corners.length)];
    }

    // 5. Take any empty cell
    const empty = this.board.map((v, i) => v === null ? i : null).filter(v => v !== null);
    return empty.length > 0 ? empty[0] : -1;
  }

  checkStatus() {
    const lines = [
      [0,1,2],[3,4,5],[6,7,8],
      [0,3,6],[1,4,7],[2,5,8],
      [0,4,8],[2,4,6]
    ];

    for (let combo of lines) {
      const [a, b, c] = combo;
      if (this.board[a] && this.board[a] === this.board[b] && this.board[a] === this.board[c]) {
        this.winner = this.board[a];
        this.winningCombo = combo;

        if (this.winner === 'X') {
          this.score += 10;
        }

        setTimeout(() => this.gameOver(), 800);
        return;
      }
    }

    if (!this.board.includes(null)) {
      this.winner = 'TIE';
      this.score += 2;
      setTimeout(() => this.gameOver(), 800);
    }
  }

  spawnCellParticles(index, color) {
    const col = index % 3;
    const row = Math.floor(index / 3);
    const cx = this.boardX + col * this.cellSize + this.cellSize / 2;
    const cy = this.boardY + row * this.cellSize + this.cellSize / 2;

    for (let i = 0; i < 14; i++) {
      const angle = Math.random() * Math.PI * 2;
      const speed = Math.random() * 2.5 + 1;
      this.particles.push({
        x: cx,
        y: cy,
        vx: Math.cos(angle) * speed,
        vy: Math.sin(angle) * speed,
        color,
        size: Math.random() * 3 + 2,
        life: 0.4
      });
    }
  }

  update(dt) {
    const input = this.input;

    if (this.state === GameState.MENU) {
      if (input.isJustPressed('Space') || input.isJustPressed('Enter')) {
        this.play();
        return;
      }
    }

    // Update particles
    for (let i = this.particles.length - 1; i >= 0; i--) {
      const pt = this.particles[i];
      pt.x += pt.vx * dt * 60;
      pt.y += pt.vy * dt * 60;
      pt.life -= dt;
      if (pt.life <= 0) {
        this.particles.splice(i, 1);
      }
    }
  }

  render(ctx) {
    // Clear canvas
    ctx.fillStyle = '#07090e';
    ctx.fillRect(0, 0, this.virtualWidth, this.virtualHeight);

    // Board container background
    ctx.save();
    ctx.fillStyle = '#121826';
    ctx.strokeStyle = '#26354d';
    ctx.lineWidth = 3;
    ctx.beginPath();
    ctx.roundRect(this.boardX - 10, this.boardY - 10, this.boardSize + 20, this.boardSize + 20, 16);
    ctx.fill();
    ctx.stroke();
    ctx.restore();

    // Turn indicator
    ctx.font = '700 16px "Orbitron", sans-serif';
    ctx.textAlign = 'center';
    if (!this.winner) {
      if (this.turn === 'X') {
        ctx.fillStyle = '#00e5ff';
        ctx.fillText("YOUR TURN (X)", this.virtualWidth / 2, this.boardY - 25);
      } else {
        ctx.fillStyle = '#9d4edd';
        ctx.fillText("AI IS THINKING (O)...", this.virtualWidth / 2, this.boardY - 25);
      }
    } else {
      ctx.fillStyle = this.winner === 'X' ? '#00e5ff' : (this.winner === 'O' ? '#ff4d4f' : '#ffbe0b');
      ctx.fillText(this.winner === 'X' ? "VICTORY!" : (this.winner === 'O' ? "AI WON!" : "DRAW MATCH!"), this.virtualWidth / 2, this.boardY - 25);
    }

    // Grid divider lines
    ctx.strokeStyle = '#26354d';
    ctx.lineWidth = 4;
    for (let i = 1; i < 3; i++) {
      // Vertical
      ctx.beginPath();
      ctx.moveTo(this.boardX + i * this.cellSize, this.boardY);
      ctx.lineTo(this.boardX + i * this.cellSize, this.boardY + this.boardSize);
      ctx.stroke();

      // Horizontal
      ctx.beginPath();
      ctx.moveTo(this.boardX, this.boardY + i * this.cellSize);
      ctx.lineTo(this.boardX + this.boardSize, this.boardY + i * this.cellSize);
      ctx.stroke();
    }

    // Hover effect
    if (this.hoverCell !== -1 && this.board[this.hoverCell] === null) {
      const col = this.hoverCell % 3;
      const row = Math.floor(this.hoverCell / 3);
      ctx.fillStyle = 'rgba(0, 229, 255, 0.08)';
      ctx.fillRect(this.boardX + col * this.cellSize, this.boardY + row * this.cellSize, this.cellSize, this.cellSize);
    }

    // Render X's and O's
    for (let i = 0; i < 9; i++) {
      const val = this.board[i];
      if (!val) continue;

      const col = i % 3;
      const row = Math.floor(i / 3);
      const cx = this.boardX + col * this.cellSize + this.cellSize / 2;
      const cy = this.boardY + row * this.cellSize + this.cellSize / 2;
      const offset = 32;

      ctx.save();
      if (val === 'X') {
        ctx.strokeStyle = '#00e5ff';
        ctx.shadowColor = '#00e5ff';
        ctx.shadowBlur = 15;
        ctx.lineWidth = 8;
        ctx.lineCap = 'round';

        ctx.beginPath();
        ctx.moveTo(cx - offset, cy - offset);
        ctx.lineTo(cx + offset, cy + offset);
        ctx.moveTo(cx + offset, cy - offset);
        ctx.lineTo(cx - offset, cy + offset);
        ctx.stroke();
      } else {
        ctx.strokeStyle = '#9d4edd';
        ctx.shadowColor = '#9d4edd';
        ctx.shadowBlur = 15;
        ctx.lineWidth = 8;

        ctx.beginPath();
        ctx.arc(cx, cy, offset, 0, Math.PI * 2);
        ctx.stroke();
      }
      ctx.restore();
    }

    // Render particles
    for (const pt of this.particles) {
      ctx.save();
      ctx.globalAlpha = pt.life / 0.4;
      ctx.fillStyle = pt.color;
      ctx.beginPath();
      ctx.arc(pt.x, pt.y, pt.size, 0, Math.PI * 2);
      ctx.fill();
      ctx.restore();
    }
  }
}
