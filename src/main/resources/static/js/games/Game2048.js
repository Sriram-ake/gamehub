/**
 * GameHub - 2048 Puzzle Game
 * Responsive 4x4 sliding tile puzzle with smooth neon styling, score tracking, and GameBase engine hooks.
 */

import { GameBase, GameState } from '../engine/GameBase.js';

export class Game2048 extends GameBase {
  constructor(canvasId = 'game-canvas') {
    super(canvasId, '2048', 800, 600);

    this.size = 4;
    this.board = [];
    this.boardSize = 420;
    this.cellSize = (this.boardSize - 50) / 4; // ~92.5px
    this.boardX = (this.virtualWidth - this.boardSize) / 2;
    this.boardY = 100;

    this.colors = {
      2: { bg: '#1c2438', text: '#f0f4f8' },
      4: { bg: '#253452', text: '#f0f4f8' },
      8: { bg: '#d35400', text: '#ffffff' },
      16: { bg: '#e74c3c', text: '#ffffff' },
      32: { bg: '#ff007f', text: '#ffffff' },
      64: { bg: '#9d4edd', text: '#ffffff' },
      128: { bg: '#7928ca', text: '#ffffff' },
      256: { bg: '#00e5ff', text: '#0b0f19' },
      512: { bg: '#00ff88', text: '#0b0f19' },
      1024: { bg: '#ffbe0b', text: '#0b0f19' },
      2048: { bg: '#ffd700', text: '#000000' }
    };
  }

  init() {
    this.reset();
  }

  reset() {
    this.score = 0;
    this.board = [
      [0, 0, 0, 0],
      [0, 0, 0, 0],
      [0, 0, 0, 0],
      [0, 0, 0, 0]
    ];
    this.spawnTile();
    this.spawnTile();
  }

  spawnTile() {
    const emptyCells = [];
    for (let r = 0; r < this.size; r++) {
      for (let c = 0; c < this.size; c++) {
        if (this.board[r][c] === 0) {
          emptyCells.push({ r, c });
        }
      }
    }
    if (emptyCells.length === 0) return false;

    const cell = emptyCells[Math.floor(Math.random() * emptyCells.length)];
    this.board[cell.r][cell.c] = Math.random() < 0.9 ? 2 : 4;
    return true;
  }

  update(dt) {
    const input = this.input;

    if (this.state === GameState.MENU) {
      if (input.isJustPressed('Space') || input.isJustPressed('Enter')) {
        this.play();
        return;
      }
    }

    if (this.state !== GameState.PLAYING) return;

    let moved = false;
    if (input.consumeJustPressed('ArrowLeft') || input.consumeJustPressed('KeyA')) {
      moved = this.moveLeft();
    } else if (input.consumeJustPressed('ArrowRight') || input.consumeJustPressed('KeyD')) {
      moved = this.moveRight();
    } else if (input.consumeJustPressed('ArrowUp') || input.consumeJustPressed('KeyW')) {
      moved = this.moveUp();
    } else if (input.consumeJustPressed('ArrowDown') || input.consumeJustPressed('KeyS')) {
      moved = this.moveDown();
    }

    if (moved) {
      this.spawnTile();
      if (this.isGameOver()) {
        this.gameOver();
      }
    }
  }

  slideAndMerge(row) {
    let arr = row.filter(val => val !== 0);
    for (let i = 0; i < arr.length - 1; i++) {
      if (arr[i] === arr[i + 1]) {
        arr[i] *= 2;
        this.score += arr[i];
        arr.splice(i + 1, 1);
      }
    }
    while (arr.length < this.size) {
      arr.push(0);
    }
    return arr;
  }

  moveLeft() {
    let changed = false;
    for (let r = 0; r < this.size; r++) {
      const orig = [...this.board[r]];
      const next = this.slideAndMerge(this.board[r]);
      this.board[r] = next;
      if (orig.some((v, idx) => v !== next[idx])) changed = true;
    }
    return changed;
  }

  moveRight() {
    let changed = false;
    for (let r = 0; r < this.size; r++) {
      const orig = [...this.board[r]];
      const reversed = [...this.board[r]].reverse();
      const next = this.slideAndMerge(reversed).reverse();
      this.board[r] = next;
      if (orig.some((v, idx) => v !== next[idx])) changed = true;
    }
    return changed;
  }

  moveUp() {
    let changed = false;
    for (let c = 0; c < this.size; c++) {
      let col = [this.board[0][c], this.board[1][c], this.board[2][c], this.board[3][c]];
      const next = this.slideAndMerge(col);
      for (let r = 0; r < this.size; r++) {
        if (this.board[r][c] !== next[r]) changed = true;
        this.board[r][c] = next[r];
      }
    }
    return changed;
  }

  moveDown() {
    let changed = false;
    for (let c = 0; c < this.size; c++) {
      let col = [this.board[3][c], this.board[2][c], this.board[1][c], this.board[0][c]];
      const next = this.slideAndMerge(col);
      for (let r = 0; r < this.size; r++) {
        if (this.board[3 - r][c] !== next[r]) changed = true;
        this.board[3 - r][c] = next[r];
      }
    }
    return changed;
  }

  isGameOver() {
    // Check for any empty cell
    for (let r = 0; r < this.size; r++) {
      for (let c = 0; c < this.size; c++) {
        if (this.board[r][c] === 0) return false;
      }
    }
    // Check horizontal merges
    for (let r = 0; r < this.size; r++) {
      for (let c = 0; c < this.size - 1; c++) {
        if (this.board[r][c] === this.board[r][c + 1]) return false;
      }
    }
    // Check vertical merges
    for (let c = 0; c < this.size; c++) {
      for (let r = 0; r < this.size - 1; r++) {
        if (this.board[r][c] === this.board[r + 1][c]) return false;
      }
    }
    return true;
  }

  render(ctx) {
    // Background
    ctx.fillStyle = '#07090e';
    ctx.fillRect(0, 0, this.virtualWidth, this.virtualHeight);

    // Board container
    ctx.save();
    ctx.shadowColor = 'rgba(0, 229, 255, 0.15)';
    ctx.shadowBlur = 20;
    ctx.fillStyle = '#121826';
    ctx.strokeStyle = '#26354d';
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.roundRect(this.boardX, this.boardY, this.boardSize, this.boardSize, 12);
    ctx.fill();
    ctx.stroke();
    ctx.restore();

    // Render cells & tiles
    const gap = 10;
    for (let r = 0; r < this.size; r++) {
      for (let c = 0; c < this.size; c++) {
        const val = this.board[r][c];
        const x = this.boardX + gap + c * (this.cellSize + gap);
        const y = this.boardY + gap + r * (this.cellSize + gap);

        // Empty tile cell background
        ctx.fillStyle = '#182234';
        ctx.beginPath();
        ctx.roundRect(x, y, this.cellSize, this.cellSize, 8);
        ctx.fill();

        if (val > 0) {
          const cfg = this.colors[val] || { bg: '#ffd700', text: '#000' };

          ctx.save();
          if (val >= 128) {
            ctx.shadowColor = cfg.bg;
            ctx.shadowBlur = 12;
          }
          ctx.fillStyle = cfg.bg;
          ctx.beginPath();
          ctx.roundRect(x, y, this.cellSize, this.cellSize, 8);
          ctx.fill();
          ctx.restore();

          // Value text
          ctx.fillStyle = cfg.text;
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          let fontSize = 32;
          if (val > 64) fontSize = 28;
          if (val > 512) fontSize = 24;
          if (val > 2048) fontSize = 20;

          ctx.font = `800 ${fontSize}px "Orbitron", sans-serif`;
          ctx.fillText(val.toString(), x + this.cellSize / 2, y + this.cellSize / 2 + 2);
        }
      }
    }
  }
}
